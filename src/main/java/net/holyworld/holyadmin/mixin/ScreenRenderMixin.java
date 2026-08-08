package net.holyworld.holyadmin.mixin;

import net.holyworld.holyadmin.clan.ClanParser;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenRenderMixin {
	@Inject(method = "method_2310", at = @At("TAIL"))
	private void holyadmin$clanOverlay(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
		ClanParser.INSTANCE.renderOverlay(context);
	}
}
