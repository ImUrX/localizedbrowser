package io.github.imurx.localizedbrowser.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.util.LocaleSearchProvider;
import net.minecraft.client.search.SearchManager;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(SearchManager.class)
public class MixinSearchManager {
    @WrapOperation(
            method = {"method_60361", "method_60350"},
            at = @At(value = "NEW", target = "net/minecraft/client/search/TextSearchProvider")
    )
    private static <T> TextSearchProvider<T> mapItemTooltip(Function<T, Stream<String>> textsGetter,
                                                            Function<T, Stream<Identifier>> identifiersGetter,
                                                            List<T> values,
                                                            Operation<TextSearchProvider> original) {
        return LocaleSearchProvider.createProvider(textsGetter, identifiersGetter, values);
    }
}
