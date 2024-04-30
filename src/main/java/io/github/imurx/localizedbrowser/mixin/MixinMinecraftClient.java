package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.access.TextSearchProviderAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.search.ReloadableSearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(at = @At("TAIL"), method = {"method_53865", "lambda$createSearchTrees$30"}, remap = false)
    private static void mapRecipeOutput(List stacks, CallbackInfoReturnable<ReloadableSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }

    @Inject(at = @At("TAIL"), method = {"method_43764", "lambda$createSearchTrees$21"}, remap = false)
    private static void mapItemTooltip(List stacks, CallbackInfoReturnable<ReloadableSearchProvider> cir) {
        ((TextSearchProviderAccess) cir.getReturnValue()).setNormalizer(true);
    }
}