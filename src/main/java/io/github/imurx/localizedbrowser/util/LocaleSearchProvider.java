package io.github.imurx.localizedbrowser.util;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.search.SearchProvider;
import net.minecraft.client.search.SuffixArray;
import net.minecraft.client.search.TextSearchProvider;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public interface LocaleSearchProvider<T> {
    static final ThreadLocal<Boolean> searchOverwrite = ThreadLocal.withInitial(() -> false);

    static <K> TextSearchProvider<K> createProvider(Function<K, Stream<String>> textsGetter,
                                                    Function<K, Stream<Identifier>> identifiersGetter,
                                                    List<K> values) {
        searchOverwrite.set(true);
        return new TextSearchProvider<>(textsGetter, identifiersGetter, values);
    }

    static <T> SearchProvider<T> normalizedPlainText(List<T> values, Function<T, Stream<String>> textsGetter) {
        if (values.isEmpty()) return SearchProvider.empty();
        SuffixArray<T> suffixArray = new SuffixArray<>();

        for (T object : values) {
            textsGetter.apply(object)
                    .flatMap(text -> LocalizedBrowser.getInstance().parseOutputs(text).stream())
                    .forEach(output -> suffixArray.add(object, output));
        }

        suffixArray.build();
        Objects.requireNonNull(suffixArray);
        return suffixArray::findAll;
    }
}
