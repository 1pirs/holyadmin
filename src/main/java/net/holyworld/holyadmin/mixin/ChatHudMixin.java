package net.holyworld.holyadmin.mixin;

import net.holyworld.holyadmin.FreezeManager;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public class ChatHudMixin {
	@Inject(method = "method_44811", at = @At("HEAD"), cancellable = true)
	private void holyadmin$markCheckMessages(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
		Text rewritten = FreezeManager.INSTANCE.rewriteCheckMessage(message);
		if (rewritten != null) {
			ChatHud self = (ChatHud) (Object) this;
			self.addMessage(rewritten, signatureData, indicator);
			ci.cancel();
		}
	}
}
