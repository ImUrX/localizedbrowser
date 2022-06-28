package io.github.interrecipebrowser.mixin;

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
        if (this.needsNormalize) {
            if (values.isEmpty()) return TextSearcher.of();
            SuffixArray<T> suffixArray = new SuffixArray<>();


            switch (InterRecipeBrowser.instance.getCurrentLanguageCode()) {
                case "ja_jp":
                    for (T object : values) {
                        textsGetter.apply(object).forEach(text -> {
                            String lowercase = text.toLowerCase(Locale.ROOT);
                            String base = InterRecipeBrowser.simplifyGraphemes(text).toLowerCase(Locale.ROOT);
                            boolean isJapanese = Wanakana.isJapanese(lowercase);

                            // Simplify to base letter
                            suffixArray.add(object, base);

                            // Simplify to romaji if it's japanese
                            if (isJapanese) {
                                suffixArray.add(object, Wanakana.toRomaji(lowercase));
                            }
                            // Return if the text is the same when simplified
                            if (lowercase.equals(base)) return;

                            // Or simplify to hiragana if it's japanese
                            if(isJapanese) {
                                suffixArray.add(object, InterRecipeBrowser.simplifyKana(lowercase));
                            }

                            // Add original
                            suffixArray.add(object, text.toLowerCase(Locale.ROOT));
                        });
                    }
                    break;
                default:
                    for (T object : values) {
                        textsGetter.apply(object).forEach(text -> {
                            String lowercase = text.toLowerCase(Locale.ROOT);
                            String base = InterRecipeBrowser.simplifyGraphemes(text).toLowerCase(Locale.ROOT);

                            // Simplify to base letter
                            suffixArray.add(object, base);

                            if (!lowercase.equals(base)) {
                                // Add original
                                suffixArray.add(object, lowercase);
                            }
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
