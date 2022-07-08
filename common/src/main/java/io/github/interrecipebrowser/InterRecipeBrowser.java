package io.github.interrecipebrowser;

import com.atilika.kuromoji.unidic.Tokenizer;
import dev.esnault.wanakana.core.Wanakana;
import io.github.interrecipebrowser.mixin.AccessorLanguageManager;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.text.Normalizer;
import java.util.Locale;
import java.util.stream.Collectors;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";
    public static final Logger LOGGER = LoggerFactory.getLogger("Localized Browser");
    private static InterRecipeBrowser instance;
    private final Tokenizer jpTokenizer = new Tokenizer();


    public static void init() {
        var mod = new InterRecipeBrowser();
        LOGGER.info("I now exist");
        instance = mod;
    }

    @Nullable
    public static InterRecipeBrowser getInstance() {
        return instance;
    }

    ;

    public String getJapaneseReading(String sentence) {
        var tokens = this.jpTokenizer.tokenize(sentence);
        return tokens.stream().map(token -> {
            String pronunciation = token.getPronunciation(),
                    reading = token.getWrittenForm();
            return Wanakana.toHiragana(InterRecipeBrowser.containsKanji(reading) ? pronunciation : reading);
        }).collect(Collectors.joining());
    }

    public static String simplifyGraphemes(String string) {
        if (Wanakana.isJapanese(string)) {
            string = simplifyKana(string);
        }
        return removeDiacritics(string);
    }

    public static String romanToNative(String string) {
        switch (((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode()) {
            case "ja_jp":
                if (Wanakana.isRomaji(string) && string.equals(string.toLowerCase(Locale.ROOT))) {
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

    public static boolean containsKanji(String string) {
        return string.codePoints().anyMatch(x -> Wanakana.isKanji(Character.toString(x)));
    }

    public static void main(String[] args) {
        init();
        System.out.println(getInstance().getJapaneseReading("木の斧"));
    }
}
