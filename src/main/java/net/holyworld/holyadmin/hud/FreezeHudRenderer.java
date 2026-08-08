package net.holyworld.holyadmin.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.holyworld.holyadmin.FreezeManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.awt.Color;

public final class FreezeHudRenderer {
	private FreezeHudRenderer() {
	}

	public static void register() {
		HudRenderCallback.EVENT.register((drawContext, tickCounter) -> render(drawContext));
	}

	private static void render(DrawContext context) {
		if (!FreezeManager.INSTANCE.isActive()) {
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null || client.getWindow() == null) {
			return;
		}

		TextRenderer tr = client.textRenderer;
		String nick = FreezeManager.INSTANCE.getNick();
		String time = FreezeManager.formatTime(FreezeManager.INSTANCE.getElapsedMs());

		float t = System.currentTimeMillis() / 1000.0f;
		float phase = (t % 2.0f) / 2.0f;

		Text nickText = RainbowText.rainbow(nick, phase, 0.02f, 0.9f, 1.0f);
		Text timeText = RainbowText.rainbow("Проверка: " + time, phase + 0.5f, 0.02f, 0.9f, 1.0f);

		int width = Math.max(tr.getWidth(nickText), tr.getWidth(timeText)) + 28;
		int screenWidth = client.getWindow().getScaledWidth();
		int x = (screenWidth - width) / 2;
		int y = 10;
		int height = 34;

		context.fill(x, y, x + width, y + height, 0x99000000);
		context.fill(x, y, x + width, y + 1, borderColor(phase));
		context.fill(x, y + height - 1, x + width, y + height, borderColor(phase + 0.5f));

		int nickX = x + (width - tr.getWidth(nickText)) / 2;
		context.drawText(tr, nickText, nickX, y + 5, -1, true);

		int timeX = x + (width - tr.getWidth(timeText)) / 2;
		context.drawText(tr, timeText, timeX, y + 19, -1, true);
	}

	private static int borderColor(float phase) {
		return 0xFF000000 | Color.HSBtoRGB(phase % 1.0f, 0.85f, 1.0f);
	}
}
