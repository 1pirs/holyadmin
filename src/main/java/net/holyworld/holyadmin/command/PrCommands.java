package net.holyworld.holyadmin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.holyworld.holyadmin.ban.BanExecutor;
import net.holyworld.holyadmin.gui.ReasonScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;

import java.util.Locale;

public final class PrCommands {
	private PrCommands() {
	}

	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommands(dispatcher));
	}

	private static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		dispatcher.register(ClientCommandManager.literal("pr")
			.then(ClientCommandManager.literal("ban")
				.executes(context -> {
					MinecraftClient.getInstance().setScreen(new ReasonScreen());
					return 1;
				})
				.then(ClientCommandManager.argument("reason", StringArgumentType.greedyString())
					.suggests((context, builder) -> {
						String remaining = builder.getRemaining().toLowerCase(Locale.ROOT);
						for (String reason : BanExecutor.REASONS) {
							if (reason.toLowerCase(Locale.ROOT).startsWith(remaining)) {
								builder.suggest(reason);
							}
						}
						return builder.buildFuture();
					})
					.executes(context -> {
						String reason = StringArgumentType.getString(context, "reason");
						if ("своя причина".equalsIgnoreCase(reason.trim())) {
							MinecraftClient.getInstance().setScreen(new ChatScreen("/pr ban "));
							return 1;
						}
						BanExecutor.ban(reason);
						return 1;
					})))
			.then(ClientCommandManager.literal("vk")
				.then(ClientCommandManager.argument("link", StringArgumentType.greedyString())
					.executes(context -> {
						BanExecutor.setVk(StringArgumentType.getString(context, "link"));
						return 1;
					})))
			.then(ClientCommandManager.literal("help")
				.executes(context -> {
					context.getSource().sendFeedback(Text.literal(
						"§6/pr ban §7— открыть меню выбора причины\n"
							+ "§6/pr ban <причина> §7— бан игрока на проверке (30д; признание — 20д)\n"
							+ "§6/pr vk <ссылка> §7— задать ссылку ВК, подставляемую в бан"));
					return 1;
				})));
	}
}
