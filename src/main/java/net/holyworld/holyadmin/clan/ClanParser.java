package net.holyworld.holyadmin.clan;

import net.holyworld.holyadmin.mixin.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ClanParser {
	public static final ClanParser INSTANCE = new ClanParser();

	private enum State { IDLE, ARMED, SCANNING, PAGING }

	private State state = State.IDLE;
	private HandledScreen<?> screen;
	private int waitTicks;
	private int armedTicks;
	private final Set<String> forbiddenFound = new LinkedHashSet<>();
	private final Map<Slot, String> forbiddenSlots = new LinkedHashMap<>();
	private List<String> pageSnapshot;
	private boolean done;

	private ClanParser() {
	}

	public void arm() {
		if (state == State.IDLE) {
			state = State.ARMED;
			armedTicks = 0;
			waitTicks = 0;
			forbiddenFound.clear();
			forbiddenSlots.clear();
			pageSnapshot = null;
			screen = null;
			done = false;
		}
	}

	public boolean isActive() {
		return state != State.IDLE;
	}

	public void tick(MinecraftClient client) {
		if (state == State.IDLE) {
			return;
		}
		if (client.player == null) {
			reset();
			return;
		}

		Screen cs = client.currentScreen;
		if (!(cs instanceof HandledScreen<?> hs)) {
			if (state == State.ARMED) {
				if (cs == null && ++armedTicks > 80) {
					reset();
				}
			} else if (cs == null) {
				reset();
			}
			return;
		}

		if (state == State.ARMED) {
			screen = hs;
			state = State.SCANNING;
			waitTicks = 10;
			return;
		}

		if (hs != screen) {
			screen = hs;
			if (state == State.PAGING) {
				state = State.SCANNING;
				waitTicks = 10;
			}
		}

		if (waitTicks > 0) {
			waitTicks--;
			return;
		}

		if (state == State.SCANNING) {
			scanPage(client);
		} else if (state == State.PAGING) {
			ScreenHandler handler = getHandler(hs);
			if (handler == null || snapshotChanged(handler)) {
				state = State.SCANNING;
				waitTicks = 4;
			} else {
				finish(client);
			}
		}
	}

	private void scanPage(MinecraftClient client) {
		HandledScreen<?> hs = screen;
		if (hs == null) {
			reset();
			return;
		}
		ScreenHandler handler = getHandler(hs);
		if (handler == null) {
			reset();
			return;
		}

		forbiddenSlots.clear();
		List<String> snap = new ArrayList<>();
		int arrowSlot = -1;

		for (Slot slot : handler.slots) {
			ItemStack stack = slot.getStack();
			if (stack.isEmpty()) {
				continue;
			}
			String id = itemId(stack);
			String name = stack.getName().getString();
			snap.add(id + "|" + name);
			if (id.equals("minecraft:spectral_arrow")) {
				arrowSlot = slot.id;
				continue;
			}
			if (stack.getCustomName() == null) {
				continue;
			}
			String clan = clanNameFrom(stack);
			if (clan == null) {
				continue;
			}
			if (ClanNameRules.INSTANCE.isForbidden(clan)) {
				forbiddenSlots.put(slot, clan);
				forbiddenFound.add(clan);
			}
		}

		pageSnapshot = snap;

		if (arrowSlot >= 0) {
			handler.onSlotClick(arrowSlot, 0, SlotActionType.PICKUP, client.player);
			state = State.PAGING;
			waitTicks = 18;
		} else {
			finish(client);
		}
	}

	private boolean snapshotChanged(ScreenHandler handler) {
		if (pageSnapshot == null) {
			return true;
		}
		List<String> cur = new ArrayList<>();
		for (Slot slot : handler.slots) {
			ItemStack stack = slot.getStack();
			if (stack.isEmpty()) {
				continue;
			}
			cur.add(itemId(stack) + "|" + stack.getName().getString());
		}
		return !cur.equals(pageSnapshot);
	}

	private void finish(MinecraftClient client) {
		if (done) {
			reset();
			return;
		}
		done = true;
		StringBuilder sb = new StringBuilder("\u00a7aРџР°СЂСЃРёРЅРі Р·Р°РІРµСЂС€РµРЅ");
		if (!forbiddenFound.isEmpty()) {
			sb.append("\u00a7c вЂ” Р·Р°РїСЂРµС‰С‘РЅРЅС‹Рµ РєР»Р°РЅС‹: \u00a7e");
			sb.append(String.join("\u00a77, \u00a7e", forbiddenFound));
		}
		feedback(client, sb.toString());
		reset();
	}

	private void reset() {
		state = State.IDLE;
		screen = null;
		waitTicks = 0;
		armedTicks = 0;
		forbiddenSlots.clear();
		pageSnapshot = null;
	}

	public void renderOverlay(DrawContext context) {
		if ((state != State.SCANNING && state != State.PAGING) || screen == null) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.currentScreen != screen) {
			return;
		}
		int ox = ((HandledScreenAccessor) screen).holyadmin$getBackgroundX();
		int oy = ((HandledScreenAccessor) screen).holyadmin$getBackgroundY();
		float t = System.currentTimeMillis() / 1000.0f;
		float pulse = 0.5f + 0.5f * (float) Math.sin(t * 10.0f);
		int alpha = 0x80 + (int) (0x70 * pulse);
		for (Slot slot : forbiddenSlots.keySet()) {
			int x = ox + slot.x - 1;
			int y = oy + slot.y - 1;
			context.fill(x, y, x + 18, y + 18, 0x55FF0000);
			context.fill(x + 1, y + 1, x + 17, y + 17, (alpha << 24) | 0xFF0000);
		}
	}

	private static ScreenHandler getHandler(HandledScreen<?> hs) {
		return (ScreenHandler) ((ScreenHandlerProvider) hs).getScreenHandler();
	}

	private static String itemId(ItemStack stack) {
		return Registries.ITEM.getId(stack.getItem()).toString();
	}

	private static String clanNameFrom(ItemStack stack) {
		String name = stack.getName().getString();
		if (name == null) {
			return null;
		}
		String line = name.split("\\R")[0];
		line = line.replaceAll("(?s)\u00a7.", "").trim();
		if (line.isEmpty()) {
			return null;
		}
		String low = line.toLowerCase(Locale.ROOT);
		if (low.startsWith("РєР»Р°РЅ:")) {
			line = line.substring(5).trim();
		} else if (low.startsWith("clan:")) {
			line = line.substring(5).trim();
		}
		return line.isEmpty() ? null : line;
	}

	private static void feedback(MinecraftClient client, String message) {
		if (client != null && client.inGameHud != null) {
			client.inGameHud.getChatHud().addMessage(Text.literal(message));
		}
	}
}

