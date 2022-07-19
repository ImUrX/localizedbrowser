package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.util.IMETextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookWidget.class)
public class MixinRecipeBookWidget {
    @ModifyVariable(
            method = "refreshResults",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z")
    )
    private String mixinString(String original) {
        return LocalizedBrowser.getInstance().parseInput(original);
    }

    @Redirect(method = "reset", at = @At(value = "NEW", target = "net/minecraft/client/gui/widget/TextFieldWidget"))
    private TextFieldWidget declareFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        return new IMETextFieldWidget(textRenderer, x, y, width, height, text);
    }
}