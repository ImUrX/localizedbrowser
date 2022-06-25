package io.github.interrecipebrowser.mixin;

import com.google.common.collect.ObjectArrays;
import dev.esnault.wanakana.core.Wanakana;
import io.github.interrecipebrowser.InterRecipeBrowser;
import io.github.interrecipebrowser.access.TextSearchProviderAccess;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.client.search.TextSearcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

@Mixin(TextSearchProvider.class)
public class MixinTextSearchProvider<T> implements TextSearchProviderAccess {
    @Final
    @Shadow
    private List<T> values;
    @Final
    @Shadow
    private Function<T, Stream<String>> textsGetter;

    @Unique
    private boolean needsNormalize = false;

    @Redirect(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/search/TextSearcher;of(Ljava/util/List;Ljava/util/function/Function;)Lnet/minecraft/client/search/TextSearcher;"))
    private TextSearcher<T> onReload(List<T> values, Function<T, Stream<String>> textsGetter) {
        if(this.needsNormalize) {
            if(values.isEmpty()) return TextSearcher.of();
            SuffixArray<T> suffixArray = new SuffixArray<>();

            for(T object : values) {
                (textsGetter.apply(object)).forEach(text -> suffixArray.add(object, InterRecipeBrowser.simplifyGraphemes(text).toLowerCase(Locale.ROOT)));
            }
            switch(InterRecipeBrowser.getCurrentLanguageCode()) {
                case "ja_jp":
                    for(T object : values) {
                        (textsGetter.apply(object)).forEach(text -> {
                            suffixArray.add(object, Wanakana.isJapanese(text) ? InterRecipeBrowser.simplifyKana(text.toLowerCase(Locale.ROOT)) : text.toLowerCase(Locale.ROOT));
                        });
                    }
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
