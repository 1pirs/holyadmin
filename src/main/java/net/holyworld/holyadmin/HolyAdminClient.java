package net.holyworld.holyadmin;

import net.fabricmc.api.ClientModInitializer;
import net.holyworld.holyadmin.command.PrCommands;
import net.holyworld.holyadmin.config.ModConfig;
import net.holyworld.holyadmin.hud.FreezeHudRenderer;

public class HolyAdminClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModConfig.INSTANCE.load();
		FreezeHudRenderer.register();
		PrCommands.register();
	}
}
