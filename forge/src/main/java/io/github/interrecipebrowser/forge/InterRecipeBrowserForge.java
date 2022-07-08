package io.github.interrecipebrowser.forge;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraftforge.fml.common.Mod;

@Mod(InterRecipeBrowser.MOD_ID)
public class InterRecipeBrowserForge {
    public InterRecipeBrowserForge() {
        InterRecipeBrowser.init();
    }
}
