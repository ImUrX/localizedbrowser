package io.github.imurx.localizedbrowser.fabric;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.fabricmc.api.ModInitializer;

public class LocalizedBrowserFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LocalizedBrowser.init();
    }
}
