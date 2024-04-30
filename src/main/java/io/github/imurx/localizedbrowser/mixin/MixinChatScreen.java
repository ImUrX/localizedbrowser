package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.util.IMETextFieldWidget;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public class MixinChatScreen {
    @Redirect(method = "Lnet/minecraft/client/gui/screen/ChatScreen;init()V", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/gui/screen/ChatScreen;chatField:Lnet/minecraft/client/gui/widget/TextFieldWidget;",
            opcode = 0xB5
    ))
    private void setChatField(ChatScreen instance, TextFieldWidget value) {
        var steal = new IMETextFieldWidget(
                ((AccessorTextFieldWidget) value).getTextRenderer(),
                value.getX(), value.getY(),
                value.getWidth(), value.getHeight(),
                value.getMessage()
        ) {
            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(((AccessorChatScreen) instance).getChatInputSuggestor().getNarration());
            }
        };
        ((AccessorChatScreen) instance).setChatField(steal);
    }
}
