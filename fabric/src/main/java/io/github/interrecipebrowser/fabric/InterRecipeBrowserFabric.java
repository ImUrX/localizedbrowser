package io.github.interrecipebrowser.fabric;

import io.github.interrecipebrowser.InterRecipeBrowser;
import io.github.interrecipebrowser.KanjiDictionary;
import net.fabricmc.api.ModInitializer;

public class InterRecipeBrowserFabric implements ModInitializer {
    private KanjiDictionary _dictionary;
    @Override
    public void onInitialize() {
        InterRecipeBrowser.init();
        _dictionary = InterRecipeBrowser.getKanjiDic();
    }
}
