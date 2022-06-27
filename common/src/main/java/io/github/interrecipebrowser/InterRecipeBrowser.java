package io.github.interrecipebrowser;

import dev.esnault.wanakana.core.Wanakana;
import net.minecraft.client.resource.language.LanguageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.text.Normalizer;
import java.util.Locale;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    private static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");
    private static String currentLanguageCode = LanguageManager.DEFAULT_LANGUAGE_CODE;
    private static WeakReference<KanjiDictionary> kanjiDic = new WeakReference<>(null);


    public static void init() {
        LOGGER.info("I now exist");
    }

    public static KanjiDictionary getKanjiDic() {
        KanjiDictionary dict = kanjiDic.get();
        if(dict != null) return dict;
        LOGGER.info("Loading kanji dictionary");
        try (InputStream is = InterRecipeBrowser.class.getResourceAsStream("/kanjidic2.json")) {
            dict = new KanjiDictionary(new InputStreamReader(is));
        } catch(IOException ex) {
            LOGGER.error("Couldn't find kanji dictionary:", ex);
            return null;
        }
        LOGGER.info("Loaded kanji dictionary");
        kanjiDic = new WeakReference<>(dict);
        return dict;
    }

    public static String getCurrentLanguageCode() {
        return currentLanguageCode;
    }

    public static void setCurrentLanguageCode(String currentLanguageCode) {
        InterRecipeBrowser.currentLanguageCode = currentLanguageCode;
    }

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
        //Maybe string builder should replace the regex part for more performance
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }
}
