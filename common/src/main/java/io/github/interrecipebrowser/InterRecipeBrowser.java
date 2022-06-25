package io.github.interrecipebrowser;

import dev.esnault.wanakana.core.IMEMode;
import dev.esnault.wanakana.core.Wanakana;
import io.github.interrecipebrowser.mixin.MixinLanguageManager;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;
import java.util.Locale;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    private static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");
    private static String currentLanguageCode = LanguageManager.DEFAULT_LANGUAGE_CODE;

    public static String simplifyGraphemes(String string) {
        if (Wanakana.isJapanese(string)) {
            string = simplifyKana(string);
        }
        return removeDiacritics(string);
    }

    public static String romanToNative(String string) {
        switch(currentLanguageCode) {
            case "ja_jp":
                if(Wanakana.isRomaji(string) && string.equals(string.toLowerCase(Locale.ROOT))) {
                    return simplifyKana(string);
                }
            default:
                return simplifyGraphemes(string);
        }
    }

    public static String simplifyKana(String string) {
        return Wanakana.toHiragana(string);
    }

    public static String removeDiacritics(String string) {
        //Maybe string builder should replace the regex part for more performance
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }

    public static void init() {
        LOGGER.info("I now exist");
    }

    public static String getCurrentLanguageCode() {
        return currentLanguageCode;
    }

    public static void setCurrentLanguageCode(String currentLanguageCode) {
        InterRecipeBrowser.currentLanguageCode = currentLanguageCode;
    }
}
