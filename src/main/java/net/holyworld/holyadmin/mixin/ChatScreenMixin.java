package net.holyworld.holyadmin.mixin;

import net.holyworld.holyadmin.FreezeManager;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
	@Inject(method = "method_44056", at = @At("HEAD"))
	private void holyadmin$onChatInput(String chatText, boolean addToHistory, CallbackInfo ci) {
		FreezeManager.INSTANCE.handleChatInput(chatText);
	}
}
