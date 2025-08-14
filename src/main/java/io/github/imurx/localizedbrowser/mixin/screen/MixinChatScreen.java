package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.util.IMEModeAccessor;
import io.github.imurx.localizedbrowser.util.IMETextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @WrapOperation(method = "init()V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatScreen;chatField:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
            opcode = 0xB5
    ))
    private void setChatField(ChatScreen instance, TextFieldWidget value, Operation<Void> original) {
        ((IMEModeAccessor) value).betterlocale$setImeMode(true);
        original.call(instance, value);
    }
}
