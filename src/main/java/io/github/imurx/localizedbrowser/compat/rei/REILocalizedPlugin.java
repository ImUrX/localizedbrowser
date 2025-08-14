package io.github.imurx.localizedbrowser.compat.rei;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.search.method.InputMethodRegistry;
import net.minecraft.util.Identifier;

public class REILocalizedPlugin implements REIClientPlugin {
    @Override
    public void registerInputMethods(InputMethodRegistry registry) {
        REIClientPlugin.super.registerInputMethods(registry);
        registry.add(Identifier.of(LocalizedBrowser.MOD_ID), new KanaInputMethod());
    }
}
