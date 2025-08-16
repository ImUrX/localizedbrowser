package io.github.imurx.localizedbrowser.fabric;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class LocalizedBrowserFabric implements ModInitializer {
    public final KeyBinding changeLocale = new KeyBinding(
            "key.localizedbrowser.locale",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            KeyBinding.UI_CATEGORY
    );

    @Override
    public void onInitialize() {
        LocalizedBrowser.init(
                FabricLoader.getInstance().getConfigDir(),
                (a) -> changeLocale.matchesKey((int) (a >> 32), (int) a)
        );
        KeyBindingHelper.registerKeyBinding(this.changeLocale);
    }
}
