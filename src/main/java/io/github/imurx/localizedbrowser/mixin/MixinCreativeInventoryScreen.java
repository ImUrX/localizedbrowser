package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.util.IMETextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {
    @Redirect(method = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;init()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/TextFieldWidget"))
    private TextFieldWidget declareFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        return new IMETextFieldWidget(textRenderer, x, y, width, height, text);
    }
}