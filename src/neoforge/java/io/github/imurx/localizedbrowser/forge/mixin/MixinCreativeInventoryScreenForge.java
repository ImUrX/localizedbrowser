package io.github.imurx.localizedbrowser.forge.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreenForge {
    @ModifyVariable(
            method = "search",
            at = @At(value = "STORE"),
            name = "s"
    )
    private String mixinSearchString(String original) {
        return LocalizedBrowser.getInstance().parseInput(original);
    }
}