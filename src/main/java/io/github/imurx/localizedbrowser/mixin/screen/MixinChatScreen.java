package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.access.IMEModeAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Shadow
    protected TextFieldWidget chatField;

    @WrapOperation(method = "init()V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatScreen;chatField:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
            opcode = 0xB5
    ))
    private void setChatField(ChatScreen instance, TextFieldWidget value, Operation<Void> original) {
        ((IMEModeAccessor) value).betterlocale$setImeMode(true);
        ((IMEModeAccessor) value).betterlocale$setChatScreen(true);
        original.call(instance, value);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void onHideChat(CallbackInfo ci) {
        var a = ((IMEModeAccessor) this.chatField);
        if (a.betterlocale$isTemporaryIme()) {
            a.betterlocale$setTemporaryIme(false);
            var locale = LocalizedBrowser.getInstance();
            if (locale.isPassthroughIme()) locale.togglePassthroughIme();
        }
    }
}
