package io.github.imurx.localizedbrowser.compat.rei;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import me.shedaniel.rei.api.client.search.method.InputMethod;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class KanaInputMethod implements InputMethod<String> {
    @Override
    public List<Locale> getMatchingLocales() {
        return InputMethod.getAllLocales();
    }

    @Override
    public Iterable<String> expendFilter(String s) {
        return Collections.singletonList(s);
    }

    @Override
    public boolean contains(String a, String b) {
        return LocalizedBrowser
                .getInstance()
                .parseOutputs(a)
                .parallelStream()
                .anyMatch(s -> s.contains(b));
    }

    @Override
    public CompletableFuture<Void> prepare(Executor executor) {
        return CompletableFuture.runAsync(() -> {
        }, executor);
    }

    @Override
    public CompletableFuture<Void> dispose(Executor executor) {
        return CompletableFuture.runAsync(() -> {
        }, executor);
    }

    @Override
    public Text getName() {
        return Text.translatable("localizedbrowser.rei.name");
    }

    @Override
    public Text getDescription() {
        return Text.translatable("localizedbrowser.rei.desc");
    }
}
