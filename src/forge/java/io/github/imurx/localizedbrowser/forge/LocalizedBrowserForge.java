package io.github.imurx.localizedbrowser.forge;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

@Mod(LocalizedBrowser.MOD_ID)
public class LocalizedBrowserForge {
    public final KeyBinding changeLocale = new KeyBinding(
            "key.localizedbrowser.locale",
            KeyConflictContext.GUI,
            KeyModifier.ALT,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            KeyBinding.UI_CATEGORY
    );

    public LocalizedBrowserForge(IEventBus modBus) {
        LocalizedBrowser.init(
                FMLPaths.CONFIGDIR.get(),
                this.changeLocale,
                () -> this.changeLocale.getKeyModifier() == KeyModifier.CONTROL
        );
        modBus.addListener(this::onRegisterKeyMappings);
    }

    public void onRegisterKeyMappings(RegisterKeyMappingsEvent ev) {
        ev.register(this.changeLocale);
    }
}
