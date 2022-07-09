package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
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
}