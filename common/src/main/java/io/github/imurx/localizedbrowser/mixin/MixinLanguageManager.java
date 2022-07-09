package io.github.imurx.localizedbrowser.mixin;

import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {
    @Inject(at = @At("RETURN"), method = "setLanguage")
    private void onSetLanguage(LanguageDefinition language, CallbackInfo ci) {
        LocalizedBrowser.getInstance().reload();
    }
}
