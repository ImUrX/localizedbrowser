package io.github.interrecipebrowser.mixin;

import io.github.interrecipebrowser.InterRecipeBrowser;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {
    @Inject(method = "setLanguage", at = @At("RETURN"))
    private void onSetLanguage(LanguageDefinition language, CallbackInfo ci) {
        InterRecipeBrowser.setCurrentLanguageCode(language.getCode());
    }
}
