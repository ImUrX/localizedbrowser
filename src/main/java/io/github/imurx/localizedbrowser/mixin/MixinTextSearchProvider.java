package io.github.imurx.localizedbrowser.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.imurx.localizedbrowser.access.TextSearchProviderAccess;
import io.github.imurx.localizedbrowser.util.LocaleSearchProvider;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.TextSearchProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;


@Mixin(TextSearchProvider.class)
public abstract class MixinTextSearchProvider<T> implements TextSearchProviderAccess {
    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/search/SearchProvider;plainText(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/search/SearchProvider;"
            )
    )
    private SearchProvider<T> onPlainText(List<T> list,
                                          Function<T, Stream<String>> function,
                                          Operation<SearchProvider<T>> original) {
        if (LocaleSearchProvider.searchOverwrite.get()) {
            LocaleSearchProvider.searchOverwrite.set(false);
            return LocaleSearchProvider.normalizedPlainText(list, function);
        } else {
            return original.call(list, function);
        }
    }
}
