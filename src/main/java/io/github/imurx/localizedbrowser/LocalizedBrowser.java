package io.github.imurx.localizedbrowser;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import dev.esnault.wanakana.core.Wanakana;
import dev.esnault.wanakana.core.utils.ImeText;
import io.github.imurx.localizedbrowser.mixin.AccessorLanguageManager;
import io.github.imurx.localizedbrowser.util.DependencyManager;
import io.github.imurx.localizedbrowser.util.IMEText;
import io.github.imurx.localizedbrowser.util.JapaneseTokenizerWrapper;
import io.netty.util.internal.UnstableApi;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.LanguageDefinition;
import org.apache.commons.lang3.function.TriFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LocalizedBrowser {
    public static final String MOD_ID = "localizedbrowser";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static LocalizedBrowser INSTANCE;
    public final Japanese japanese = new Japanese();
    public final DependencyManager manager;
    public final KeyBinding changeLocale;
    public final BooleanSupplier usesCtrl;
    private boolean passthroughIme = false;
    public static Map<LanguageDefinition, String> REVERSE_LANGUAGE_LOOKUP = ImmutableMap.of();


    protected LocalizedBrowser(Path configDir, KeyBinding changeLocale, BooleanSupplier usesCtrl) {
        this.changeLocale = changeLocale;
        this.usesCtrl = usesCtrl;
        this.manager = new DependencyManager(configDir.resolve(MOD_ID + "/cache"));
    }

    /**
     * Should only be called by the mod's own loaders
     *
     * @hidden
     */
    public static void init(Path configDir, KeyBinding changeLocale, BooleanSupplier usesCtrl) {
        var mod = new LocalizedBrowser(configDir, changeLocale, usesCtrl);
        LOGGER.info("I now exist");
        INSTANCE = mod;
    }

    public static LocalizedBrowser getInstance() {
        return INSTANCE;
    }

    public void reload() {
        LOGGER.info("Reloading...");
        setPassthroughIme(false);
        this.japanese.reload();
    }

    public void forceMemoize(String langCode) {
        if (langCode.equals("ja_jp")) {
            this.japanese.tokenizer.get();
        }
    }

    /**
     * Used for parsing inputted text through IME of current language
     *
     * @param string The input text (all of it)
     * @param start the cursor/selection start, or -1 if there is no cursor/selection.
     * @param end the cursor/selection end (inclusive), or -1 if there is no cursor/selection. If
     * the [end] is equals to the [start], it's a cursor, otherwise it's a selection.
     * @return Modified text based on the language
     */
    @UnstableApi
    public IMEText imeParser(String string, int start, int end) {
        return imeParser(string, start, end, ((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode());
    }

    /**
     * Used for parsing inputted text through IME of current language
     *
     * @param string The input text (all of it)
     * @param start the cursor/selection start, or -1 if there is no cursor/selection.
     * @param end the cursor/selection end (inclusive), or -1 if there is no cursor/selection. If
     * the [end] is equals to the [start], it's a cursor, otherwise it's a selection.
     * @param languageDefinition The language being used
     * @return Modified text based on the language
     */
    @UnstableApi
    public IMEText imeParser(String string, int start, int end, LanguageDefinition languageDefinition) {
        return imeParser(string, start, end, REVERSE_LANGUAGE_LOOKUP.get(languageDefinition));
    }

    // FIXME: Noooo boxing of primitives
    private final Map<String, TriFunction<String, Integer, Integer, IMEText>> imeParsers = ImmutableMap.of(
            "ja_jp", Japanese::imeParser
    );

    @UnstableApi
    public IMEText imeParser(String string, int start, int end, String code) {
        if (this.imeParsers.containsKey(code)) return this.imeParsers.get(code).apply(string, start, end);
        return new IMEText(string, start, end);
    }

    public boolean hasImeParser() {
        return this.imeParsers.containsKey(((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode());
    }

    public boolean hasImeParser(LanguageDefinition languageDefinition) {
        return this.imeParsers.containsKey(REVERSE_LANGUAGE_LOOKUP.get(languageDefinition));
    }

    public boolean hasImeParser(String code) {
        return this.imeParsers.containsKey(code);
    }

    /**
     * Used for general parsing of user input, using the currently selected language the game is using.
     *
     * @param string The user's input
     * @return The processed input
     */
    public String parseInput(String string) {
        return parseInput(string, ((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode());
    }

    /**
     * Used for general parsing of user input.
     *
     * @param string             The user's input
     * @param languageDefinition The language being used
     * @return The processed input
     */
    public String parseInput(String string, LanguageDefinition languageDefinition) {
        return parseInput(string, REVERSE_LANGUAGE_LOOKUP.get(languageDefinition));
    }

    private final Map<String, Function<String, String>> inputParsers = ImmutableMap.of(
            "ja_jp", Japanese::parseInput
    );

    private String parseInput(String string, String code) {
        if (this.inputParsers.containsKey(code)) return this.inputParsers.get(code).apply(string);
        return string;
    }

    /**
     * Used for general parsing of game text (ex: item/block names, tooltips, etc.), using the currently selected language the game is using.
     * Should be used with a cache, so it becomes less CPU intensive.
     *
     * @param string The text
     * @return A list of all possible inputs the user could do
     */
    public List<String> parseOutputs(String string) {
        return parseOutputs(string, ((AccessorLanguageManager) MinecraftClient.getInstance().getLanguageManager()).getCurrentLanguageCode());
    }

    /**
     * Used for general parsing of game text (ex: item/block names, tooltips, etc.).
     * Should be used with a cache, so it becomes less CPU intensive.
     *
     * @param string             The text
     * @param languageDefinition The language being used
     * @return A list of all possible inputs the user could do
     */
    public List<String> parseOutputs(String string, LanguageDefinition languageDefinition) {
        return parseOutputs(string, REVERSE_LANGUAGE_LOOKUP.get(languageDefinition));
    }

    private final Map<String, Function<String, List<String>>> outputParsers = ImmutableMap.of(
            "ja_jp", japanese::parseOutputs
    );

    private List<String> parseOutputs(String string, String code) {
        if (this.outputParsers.containsKey(code)) return this.outputParsers.get(code).apply(string);
        return Common.parseOutputs(string);
    }

    public boolean isPassthroughIme() {
        return passthroughIme;
    }

    private void setPassthroughIme(boolean bool) {
        passthroughIme = bool;
    }

    public boolean togglePassthroughIme() {
        setPassthroughIme(!isPassthroughIme());
        return isPassthroughIme();
    }


    public static class Common {
        /**
         * Removes all UTF-8 defined diacritics
         */
        public static String removeDiacritics(String string) {
            // Maybe string builder should replace the regex part for more performance
            return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        }

        /**
         * Simplifies graphemes, it's relative to the type of text being given currently.
         */
        public static String simplifyGraphemes(String string) {
            if (Wanakana.isJapanese(string)) {
                string = Japanese.simplifyKana(string);
            }
            return removeDiacritics(string);
        }

        public static List<String> parseOutputs(String text) {
            List<String> array = new ArrayList<>();
            String lowercase = text.toLowerCase(Locale.ROOT);
            String base = Common.simplifyGraphemes(text).toLowerCase(Locale.ROOT);

            // Simplify to base letter
            array.add(base);

            if (!lowercase.equals(base)) {
                // Add original
                array.add(lowercase);
            }
            return array;
        }
    }

    public static class Japanese {
        private Supplier<JapaneseTokenizerWrapper> tokenizer;

        public Japanese() {
            this.reload();
        }

        void reload() {
            this.tokenizer = Suppliers.memoize(() -> {
                try {
                    LocalizedBrowser.getInstance().manager.loadFromResource("/runtimeDownload.txt").get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
                return new JapaneseTokenizerWrapper(LocalizedBrowser.getInstance().manager.classLoader);
            });
        }

        /**
         * Checks if string contains any kanji
         */
        public static boolean containsKanji(String string) {
            return string.codePoints().anyMatch(x -> Wanakana.isKanji(Character.toString(x)));
        }

        /**
         * Simplifies the text to hiragana, no matter the case.
         */
        public static String simplifyKana(String string) {
            return Wanakana.toHiragana(string);
        }

        /**
         * For usage in {@link LocalizedBrowser#parseInput(String, String) LocalizedBrowser.parseInput()}
         */
        public static String parseInput(String string) {
            if (Wanakana.isRomaji(string) && string.equals(string.toLowerCase(Locale.ROOT))) {
                // Pass it to katakana to support choonpu which is used in romaji it seems
                return Wanakana.toRomaji(Wanakana.toKatakana(string));
            }
            return string;
        }

        /**
         * Tokenizes the string to check for kanji and parses the whole string to hiragana
         *
         * @param sentence Sentence containing kanji
         * @return Writeable sentence in hiragana
         */
        public List<String> getJapaneseReading(String sentence) {
            var tokens = this.tokenizer.get().tokenize(sentence);
            var options = tokens.stream().map(token -> {
                List<String> strings = new ArrayList<>();
                String pronunciation = token.getPronunciation(),
                        surface = token.getSurface();
                if(!Japanese.containsKanji(surface)) {
                    strings.add(surface);
                } else {
                    String hiragana = Wanakana.toHiragana(pronunciation);
                    String romaji = Wanakana.toHiragana(Wanakana.toRomaji(pronunciation));
                    strings.add(hiragana);
                    if(!hiragana.equals(romaji)) strings.add(romaji);
                }
                return strings;
            }).toList();
            return Lists.cartesianProduct(options).stream().map(x -> String.join("", x)).collect(Collectors.toList());
        }

        public List<String> parseOutputs(String text) {
            List<String> array = new ArrayList<>();
            String lowercase = text.toLowerCase(Locale.ROOT);
            String base = Common.simplifyGraphemes(text).toLowerCase(Locale.ROOT);
            boolean isJapanese = Wanakana.isJapanese(lowercase);
            boolean containsKanji = Japanese.containsKanji(lowercase);
            List<String> kanji = containsKanji ? this.getJapaneseReading(lowercase) : null;
            //TODO kanji parser doesnt give base letters (ex: 音符ブロック you can't do おんぷふろっく currently)

            // Simplify to base letter
            array.add(base);

            // Simplify to romaji if it's japanese
            if (isJapanese) {
                array.add(Wanakana.toRomaji(lowercase));

                // Add kanji in hiragana and romaji
                if (containsKanji) {
                    array.addAll(kanji);
                    array.addAll(kanji.stream().map(Wanakana::toRomaji).toList());
                }
            }

            // Return if the text is the same when simplified
            if (lowercase.equals(base)) return array;

            // Or simplify to hiragana if it's japanese
            if (isJapanese) {
                array.add(Japanese.simplifyKana(lowercase));

                // Add kanji sentence wholly in hiragana
                if (containsKanji) {
                    array.addAll(kanji.stream().map(Japanese::simplifyKana).toList());
                }
            }

            // Add original
            array.add(text.toLowerCase(Locale.ROOT));
            return array;
        }

        @UnstableApi
        public static IMEText imeParser(String string, int start, int end) {
            var ime = Wanakana.toKanaIme(new ImeText(string, Math.min(start, end), Math.max(start, end)));
            return new IMEText(ime.getText(), ime.getSelection());
        }
    }
}
