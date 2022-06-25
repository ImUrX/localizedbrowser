package io.github.interrecipebrowser.forge.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreenForge {
    @ModifyVariable(
            method = "search",
            at = @At(value = "STORE"),
            name = "search"
    )
    private String mixinNonSearchString(String original) {
        return InterRecipeBrowser.romanToNative(original);
    }

    @ModifyVariable(
            method = "search",
            at = @At(value = "STORE"),
            name = "s"
    )
    private String mixinSearchString(String original) {
        return InterRecipeBrowser.romanToNative(original);
    }
}