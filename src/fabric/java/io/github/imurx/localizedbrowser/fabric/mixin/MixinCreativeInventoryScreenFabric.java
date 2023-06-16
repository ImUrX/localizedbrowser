package io.github.imurx.localizedbrowser.fabric.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreenFabric {
    @ModifyVariable(
            method = "search",
            at = @At(value = "INVOKE", target = "Ljava/lang/String;isEmpty()Z")
    )
    private String mixinString(String original) {
        return LocalizedBrowser.getInstance().parseInput(original);
    }
}