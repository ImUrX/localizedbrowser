package io.github.imurx.localizedbrowser.forge;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

@Mod(LocalizedBrowser.MOD_ID)
public class LocalizedBrowserForge {
    public final KeyBinding changeLocale = new KeyBinding(
            "key.localizedbrowser.locale",
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            "key.categories.localizedbrowser"
    );
    public LocalizedBrowserForge() {
        LocalizedBrowser.init(FMLPaths.CONFIGDIR.get(), this.changeLocale);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterKeyMappings);
    }

    public void onRegisterKeyMappings(RegisterKeyMappingsEvent ev) {
        ev.register(this.changeLocale);
    }
}
