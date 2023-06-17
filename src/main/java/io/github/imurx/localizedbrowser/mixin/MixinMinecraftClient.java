package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.access.TextSearchProviderAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.search.TextSearchProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("TAIL"), method = {"lambda$createSearchTrees$19", "method_43760", "m_231385_"}, remap = false)
    private static void mapRecipeOutput(List resultCollections, CallbackInfoReturnable<TextSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }

    @Inject(at = @At("TAIL"), method = {"lambda$createSearchTrees$10", "method_43764", "m_91316_"}, remap = false)
    private static void mapItemTooltip(List stacks, CallbackInfoReturnable<TextSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }
}