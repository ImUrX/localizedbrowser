package io.github.interrecipebrowser.fabric;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.fabricmc.api.ModInitializer;

public class ExampleModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        InterRecipeBrowser.init();
    }
}
