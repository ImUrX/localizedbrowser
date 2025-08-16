package io.github.imurx.localizedbrowser.mixin.screen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.access.IMEModeAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RecipeBookWidget.class)
public class MixinRecipeBookWidget {
    @ModifyVariable(
            method = "refreshResults",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z")
    )
    private String mixinString(String original) {
        return LocalizedBrowser.getInstance().parseInput(original);
    }

    @WrapOperation(method = "reset", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/TextFieldWidget"))
    private TextFieldWidget declareFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, Operation<TextFieldWidget> original) {
        var field = original.call(textRenderer, x, y, width, height, text);
        ((IMEModeAccessor) field).betterlocale$setImeMode(true);
        return field;
    }
}