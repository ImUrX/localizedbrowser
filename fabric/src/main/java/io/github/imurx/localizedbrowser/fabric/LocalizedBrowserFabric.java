package io.github.imurx.localizedbrowser.fabric;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class LocalizedBrowserFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LocalizedBrowser.init(FabricLoader.getInstance().getConfigDir());
    }
}
