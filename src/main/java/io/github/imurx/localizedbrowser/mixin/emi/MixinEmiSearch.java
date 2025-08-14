package io.github.imurx.localizedbrowser.mixin.emi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.search.EmiSearch;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.search.SuffixArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(EmiSearch.class)
public class MixinEmiSearch {
    @WrapOperation(method = "bake", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/search/SuffixArray;add(Ljava/lang/Object;Ljava/lang/String;)V",
            ordinal = 0
    ))
    private static <T> void onAddNames(SuffixArray<T> instance, T object, String text, Operation<Void> original) {
        for (String s : LocalizedBrowser.getInstance().parseOutputs(text)) {
            instance.add(object, s);
        }
    }

    @WrapOperation(method = "bake", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/search/SuffixArray;add(Ljava/lang/Object;Ljava/lang/String;)V",
            ordinal = 1
    ))
    private static <T> void onAddTooltips(SuffixArray<T> instance, T object, String text, Operation<Void> original) {
        for (String s : LocalizedBrowser.getInstance().parseOutputs(text)) {
            instance.add(object, s);
        }
    }
}
