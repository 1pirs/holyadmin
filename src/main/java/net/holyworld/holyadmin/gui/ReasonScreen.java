package net.holyworld.holyadmin.gui;

import net.holyworld.holyadmin.FreezeManager;
import net.holyworld.holyadmin.ban.BanExecutor;
import net.holyworld.holyadmin.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ReasonScreen extends Screen {
	public ReasonScreen() {
		super(Text.literal("HolyWorld | Выбор причины"));
	}

	@Override
	protected void init() {
		int bw = 150;
		int bh = 20;
		int gap = 8;
		int totalW = bw * 2 + gap;
		int startX = (this.width - totalW) / 2;
		int topY = this.height / 2 - 45;

		String[] fixed = {"время вышло", "отказ", "признание", "неадекват", "autobuy", "automine"};
		for (int i = 0; i < fixed.length; i++) {
			int col = i % 2;
			int row = i / 2;
			int x = startX + col * (bw + gap);
			int y = topY + row * (bh + gap);
			String reason = fixed[i];
			this.addDrawableChild(ButtonWidget.builder(Text.literal(reason), button -> {
				this.close();
				BanExecutor.ban(reason);
			}).dimensions(x, y, bw, bh).build());
		}

		int customY = topY + 3 * (bh + gap);
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Своя причина"), button -> {
			this.close();
			MinecraftClient.getInstance().setScreen(new ChatScreen("/pr ban "));
		}).dimensions(startX, customY, totalW, bh).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, this.width, this.height, 0xCC0B0B1A);
		super.render(context, mouseX, mouseY, delta);

		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§lHolyWorld | Проверка"),
			this.width / 2, this.height / 2 - 95, 0xFFFFFF);

		String status;
		if (!FreezeManager.INSTANCE.isActive()) {
			status = "§7Нет игрока на проверке";
		} else {
			status = "§6" + FreezeManager.INSTANCE.getNick()
				+ " §7| время: §e" + FreezeManager.formatTime(FreezeManager.INSTANCE.getElapsedMs());
		}
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(status),
			this.width / 2, this.height / 2 - 80, 0xFFFFFF);

		String vk = ModConfig.INSTANCE.getVk();
		String vkLine = vk.isEmpty()
			? "§cВК не задан: /pr vk <ссылка>"
			: "§7ВК: §b" + vk;
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal(vkLine),
			this.width / 2, this.height / 2 - 68, 0xFFFFFF);

		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§7Esc — отмена"),
			this.width / 2, this.height - 30, 0xFFFFFF);
	}
}
