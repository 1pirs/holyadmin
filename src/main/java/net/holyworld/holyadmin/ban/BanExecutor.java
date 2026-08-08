package net.holyworld.holyadmin.ban;

import net.holyworld.holyadmin.FreezeManager;
import net.holyworld.holyadmin.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.Locale;

public final class BanExecutor {
	public static final String[] REASONS = {
		"время вышло", "отказ", "признание", "своя причина", "неадекват", "autobuy", "automine"
	};

	private BanExecutor() {
	}

	public static void ban(String rawReason) {
		MinecraftClient client = MinecraftClient.getInstance();

		String nick = FreezeManager.INSTANCE.getNick();
		if (nick == null || nick.isEmpty()) {
			feedback(client, "§cНет игрока на проверке. Сначала вызови: §6/freezing <ник>");
			return;
		}

		String reason = normalizeReason(rawReason);
		if (reason.isEmpty()) {
			feedback(client, "§cПричина не указана. Жми Tab для подсказок или открой меню: §6/pr ban");
			return;
		}

		String vk = ModConfig.INSTANCE.getVk();
		if (vk.isEmpty()) {
			feedback(client, "§cСсылка ВК не задана. Настрой один раз: §6/pr vk <ссылка>");
			return;
		}

		String duration = "признание".equalsIgnoreCase(reason) ? "20d" : "30d";
		String command = "banip " + nick + " " + duration + " 2.4 (" + reason + ") | Вопросы? " + vk;

		ClientPlayNetworkHandler net = client.getNetworkHandler();
		if (net == null) {
			feedback(client, "§cТы не подключён к серверу.");
			return;
		}

		net.sendCommand(command);
		net.sendCommand("freezing " + nick);
		FreezeManager.INSTANCE.clear();

		feedback(client, "§aБан отправлен: §e/" + command);
	}

	public static String normalizeReason(String raw) {
		if (raw == null) {
			return "";
		}
		String s = raw.trim();
		if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
			s = s.substring(1, s.length() - 1).trim();
		}
		String lower = s.toLowerCase(Locale.ROOT);
		for (String r : REASONS) {
			if (r.toLowerCase(Locale.ROOT).equals(lower)) {
				return r;
			}
		}
		return s;
	}

	public static void setVk(String link) {
		ModConfig.INSTANCE.setVk(link);
		feedback(MinecraftClient.getInstance(), "§aСсылка ВК сохранена: §e" + link);
	}

	private static void feedback(MinecraftClient client, String message) {
		if (client != null && client.inGameHud != null) {
			client.inGameHud.getChatHud().addMessage(Text.literal(message));
		}
	}
}
