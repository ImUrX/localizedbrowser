package io.github.imurx.localizedbrowser.mixin;

import com.google.common.collect.ImmutableMap;
import io.github.imurx.localizedbrowser.LocalizedBrowser;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {
    @Shadow
    private Map<String, LanguageDefinition> languageDefs;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void onInit(String languageCode, CallbackInfo ci) {
        LocalizedBrowser.getInstance().forceMemoize(languageCode);
    }

    @Inject(at = @At("RETURN"), method = "setLanguage")
    private void onSetLanguage(String languageCode, CallbackInfo ci) {
        LocalizedBrowser.getInstance().reload();
    }

    @Inject(at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/resource/language/LanguageManager;languageDefs:Ljava/util/Map;",
            shift = At.Shift.AFTER
    ), method = "reload")
    private void onReload(ResourceManager manager, CallbackInfo ci) {
        ImmutableMap.Builder<LanguageDefinition, String> builder = ImmutableMap.builderWithExpectedSize(languageDefs.size());
        builder.putAll(languageDefs.entrySet().stream().map(entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getValue(), entry.getKey())).collect(Collectors.toSet()));
        LocalizedBrowser.REVERSE_LANGUAGE_LOOKUP = builder.build();
    }
}
