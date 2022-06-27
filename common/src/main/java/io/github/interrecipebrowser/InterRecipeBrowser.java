package io.github.interrecipebrowser;

import dev.esnault.wanakana.core.Wanakana;
import net.minecraft.client.resource.language.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.Locale;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    private static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");
    private String currentLanguageCode = LanguageManager.DEFAULT_LANGUAGE_CODE;
    public static final InterRecipeBrowser instance = new InterRecipeBrowser();
    private KanjiDictionary kanjiDic;


    public void init() {
        LOGGER.info("I now exist");

        LOGGER.info("Loading kanji dictionary");
        try (InputStreamReader is = new InputStreamReader(InterRecipeBrowser.class.getResourceAsStream("/kanjidic2.json"))) {
            this.kanjiDic = new KanjiDictionary(is);
        } catch(IOException ex) {
            LOGGER.error("Couldn't load kanji dictionary:", ex);
            return;
        }
        LOGGER.info("Loaded kanji dictionary");
    }

    public String getCurrentLanguageCode() {
        return currentLanguageCode;
    }

    public void setCurrentLanguageCode(String currentLanguageCode) {
        this.currentLanguageCode = currentLanguageCode;
    }

    public static String simplifyGraphemes(String string) {
        if (Wanakana.isJapanese(string)) {
            string = simplifyKana(string);
        }
        return removeDiacritics(string);
    }

    public static String romanToNative(String string) {
        switch(instance.currentLanguageCode) {
            case "ja_jp":
                if(Wanakana.isRomaji(string) && string.equals(string.toLowerCase(Locale.ROOT))) {
                    // Pass it to katakana to support choonpu which is used in romaji it seems
                    return Wanakana.toRomaji(Wanakana.toKatakana(string));
                }
            default:
                return string;
        }
    }

    public static String simplifyKana(String string) {
        return Wanakana.toHiragana(string);
    }

    public static String removeDiacritics(String string) {
        // Maybe string builder should replace the regex part for more performance
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }
}
