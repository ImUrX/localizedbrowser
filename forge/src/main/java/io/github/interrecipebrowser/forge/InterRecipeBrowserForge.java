package io.github.interrecipebrowser.forge;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraftforge.fml.common.Mod;

@Mod(InterRecipeBrowser.MOD_ID)
public class InterRecipeBrowserForge {
    public InterRecipeBrowserForge() {
        // Submit our event bus to let architectury register our content on the right time
        InterRecipeBrowser.init();
    }
}