package net.holyworld.holyadmin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.holyworld.holyadmin.clan.ClanParser;
import net.holyworld.holyadmin.command.PrCommands;
import net.holyworld.holyadmin.config.ModConfig;
import net.holyworld.holyadmin.hud.FreezeHudRenderer;

public class HolyAdminClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModConfig.INSTANCE.load();
		FreezeHudRenderer.register();
		PrCommands.register();
		ClientTickEvents.END_CLIENT_TICK.register(client -> ClanParser.INSTANCE.tick(client));
	}
}
