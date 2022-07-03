package io.github.interrecipebrowser;

import dev.esnault.wanakana.core.Wanakana;
import io.github.interrecipebrowser.mixin.AccessorLanguageManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    private static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");
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

    public KanjiDictionary getKanjiDictionary() {
        return this.kanjiDic;
    }

    public List<String> kanjiToHiragana(String sentence) {
        List<String> array = new ArrayList<>();
        if(!Wanakana.isJapanese(sentence)) return array;
        List<String> tokens = Wanakana.tokenize(sentence);
        // todo add kanji processing
        return array;
    }

    public static String simplifyGraphemes(String string) {
        if (Wanakana.isJapanese(string)) {
            string = simplifyKana(string);
        }
        return removeDiacritics(string);
    }

    public static String romanToNative(String string) {
        switch(((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode()) {
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
