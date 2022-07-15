package io.github.imurx.localizedbrowser.util;

import java.util.List;

public class JapaneseTokenizerWrapper {
    public final Class<?> aClass;
    public final Object ptr;
    private final ClassLoader loader;

    public JapaneseTokenizerWrapper(ClassLoader loader) {
        this.loader = loader;
        try {
            this.aClass = Class.forName("com.atilika.kuromoji.ipadic.Tokenizer", true, this.loader);
            this.ptr = this.aClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TokenWrapper> tokenize(String string) {
        try {
            return ((List<?>) this.aClass.getMethod("tokenize", String.class)
                    .invoke(this.ptr, string))
                    .stream()
                    .map(ptr -> new TokenWrapper(ptr, this.loader))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class TokenWrapper {
        public final Class<?> aClass;
        public final Object ptr;

        protected TokenWrapper(Object ptr, ClassLoader classLoader) {
            try {
                this.aClass = Class.forName("com.atilika.kuromoji.ipadic.Token", true, classLoader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.ptr = ptr;
        }

        public String getPronunciation() {
            try {
                return (String) this.aClass.getMethod("getPronunciation").invoke(this.ptr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getReading() {
            try {
                return (String) this.aClass.getMethod("getReading").invoke(this.ptr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getSurface() {
            try {
                return (String) this.aClass.getMethod("getSurface").invoke(this.ptr);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
