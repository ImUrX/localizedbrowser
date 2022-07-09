package io.github.imurx.localizedbrowser.forge;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraftforge.fml.common.Mod;

@Mod(LocalizedBrowser.MOD_ID)
public class LocalizedBrowserForge {
    public LocalizedBrowserForge() {
        LocalizedBrowser.init();
    }
}
