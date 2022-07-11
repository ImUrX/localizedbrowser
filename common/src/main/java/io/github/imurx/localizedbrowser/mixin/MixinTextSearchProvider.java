package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import io.github.imurx.localizedbrowser.access.TextSearchProviderAccess;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.client.search.TextSearcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(TextSearchProvider.class)
public class MixinTextSearchProvider<T> implements TextSearchProviderAccess {
    @Unique
    private boolean needsNormalize = false;

    @Redirect(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/search/TextSearcher;of(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/search/TextSearcher;"))
    private TextSearcher<T> onReload(List<T> values, Function<T, Stream<String>> textsGetter) {
        if (this.needsNormalize) {
            if (values.isEmpty()) return TextSearcher.of();
            SuffixArray<T> suffixArray = new SuffixArray<>();

            for (T object : values) {
                textsGetter.apply(object)
                        .flatMap(text -> LocalizedBrowser.getInstance().parseOutputs(text).stream())
                        .forEach(output -> suffixArray.add(object, output));
            }

            suffixArray.build();
            return suffixArray::findAll;
        }
        return TextSearcher.of(values, textsGetter);
    }

    @Override
    public boolean isNormalizer() {
        return this.needsNormalize;
    }

    @Override
    public void setNormalizer(boolean bool) {
        this.needsNormalize = bool;
    }
}
