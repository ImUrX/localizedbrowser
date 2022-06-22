package io.github.interrecipebrowser;

import dev.esnault.wanakana.core.IMEMode;
import dev.esnault.wanakana.core.Wanakana;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Normalizer;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    private static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");

    public static String simplifyGraphemes(String string) {
        boolean romaji;
        if ((romaji = Wanakana.isRomaji(string)) || Wanakana.isJapanese(string)) {
            string = simplifyKana(string);
            if (romaji) return string;
        }
        return removeDiacritics(string);
    }

    public static String simplifyKana(String string) {
        return Wanakana.toHiragana(string, IMEMode.TO_HIRAGANA);
    }

    public static String removeDiacritics(String string) {
        //Maybe string builder should replace the regex part for more performance
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }

    public static void init() {
        LOGGER.info("I now exist");
    }
}
