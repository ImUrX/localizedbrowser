package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.util.IMEModeAccessor;
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
    @WrapOperation(method = "init()V", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/TextFieldWidget"))
    private TextFieldWidget declareFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Operation<TextFieldWidget> original) {
        var field = original.call(textRenderer, x, y, width, height, text);
        ((IMEModeAccessor) field).betterlocale$setImeMode(true);
        return field;
    }
}