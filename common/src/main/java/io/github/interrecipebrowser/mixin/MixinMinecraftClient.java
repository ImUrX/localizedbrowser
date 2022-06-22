package io.github.interrecipebrowser.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("TAIL"), method = {"lambda$createSearchTrees$15", "method_43761"}, cancellable = true, remap = false)
    private static void mapRecipeOutput(RecipeResultCollection resultCollection, CallbackInfoReturnable<Stream<String>> cir) {
        cir.setReturnValue(cir.getReturnValue().map(InterRecipeBrowser::simplifyGraphemes));
    }
}