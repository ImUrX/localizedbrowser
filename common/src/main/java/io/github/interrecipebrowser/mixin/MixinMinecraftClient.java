package io.github.interrecipebrowser.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import io.github.interrecipebrowser.access.TextSearchProviderAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.search.ReloadableSearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("TAIL"), method = {"lambda$createSearchTrees$18", "method_43760"}, remap = false)
    private static void mapRecipeOutput(List resultCollections, CallbackInfoReturnable<TextSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }

    @Inject(at = @At("TAIL"), method = {"lambda$createSearchTrees$9", "method_43764"}, remap = false)
    private static void mapItemTooltip(List stacks, CallbackInfoReturnable<TextSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }
}