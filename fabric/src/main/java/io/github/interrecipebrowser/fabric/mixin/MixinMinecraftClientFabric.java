package io.github.interrecipebrowser.fabric.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClientFabric {
    @Inject(at = @At("TAIL"), method = "method_1485", cancellable = true)
    private static void mapItemTooltip(ItemStack stack, CallbackInfoReturnable<Stream<String>> cir) {
        cir.setReturnValue(cir.getReturnValue().map(InterRecipeBrowser::simplifyGraphemes));
    }
}
