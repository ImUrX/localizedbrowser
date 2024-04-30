package io.github.imurx.localizedbrowser.fabric;

import de.siphalor.amecs.api.AmecsKeyBinding;
import de.siphalor.amecs.api.KeyModifiers;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class LocalizedBrowserFabric implements ModInitializer {
    public final KeyBinding changeLocale = new AmecsKeyBinding(
            "key.localizedbrowser.locale",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            KeyBinding.UI_CATEGORY,
            new KeyModifiers(true, false, false)
    );

    @Override
    public void onInitialize() {
        LocalizedBrowser.init(FabricLoader.getInstance().getConfigDir(), changeLocale);
        KeyBindingHelper.registerKeyBinding(this.changeLocale);
    }
}
