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
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@Mod(LocalizedBrowser.MOD_ID)
public class LocalizedBrowserForge {
    public final Lazy<KeyBinding> changeLocale = Lazy.of(() -> new KeyBinding(
            "key.localizedbrowser.locale",
            KeyConflictContext.GUI,
            KeyModifier.ALT,
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_SPACE,
            KeyBinding.UI_CATEGORY
    ));

    public LocalizedBrowserForge(IEventBus modBus) {
        LocalizedBrowser.init(
                FMLPaths.CONFIGDIR.get(),
                (a) -> this.changeLocale.get().isActiveAndMatches(InputUtil.fromKeyCode((int) (a >> 32), (int) a))
        );
        modBus.addListener(this::onRegisterKeyMappings);
    }

    public void onRegisterKeyMappings(RegisterKeyMappingsEvent ev) {
        ev.register(this.changeLocale.get());
    }
}
