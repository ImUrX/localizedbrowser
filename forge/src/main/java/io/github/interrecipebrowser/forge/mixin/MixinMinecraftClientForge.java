package io.github.interrecipebrowser.forge.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClientForge {
    @Inject(at = @At("TAIL"), method = "lambda$createSearchTrees$5", cancellable = true)
    private static void mapItemTooltip(Text text, CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(InterRecipeBrowser.removeDiacritics(cir.getReturnValue()));
    }
}
