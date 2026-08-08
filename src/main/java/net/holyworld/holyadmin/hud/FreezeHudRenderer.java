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
		Text line = ShimmerText.shimmerBlue("Проверка: " + nick + "  " + time, t);

		int width = tr.getWidth(line) + 28;
		int screenWidth = client.getWindow().getScaledWidth();
		int x = (screenWidth - width) / 2;
		int y = client.getWindow().getScaledHeight() - 40;
		int height = 20;

		context.fill(x, y, x + width, y + height, 0x99000000);
		context.fill(x, y, x + width, y + 1, borderColor(t));
		context.fill(x, y + height - 1, x + width, y + height, borderColor(t + 0.5f));

		int textX = x + (width - tr.getWidth(line)) / 2;
		int textY = y + (height - tr.fontHeight) / 2;
		context.drawText(tr, line, textX, textY, -1, true);
	}

	private static int borderColor(float t) {
		float brightness = 0.75f + 0.25f * (0.5f + 0.5f * (float) Math.sin(t * 3.0f));
		return 0xFF000000 | Color.HSBtoRGB(0.58f, 0.9f, brightness);
	}
}
