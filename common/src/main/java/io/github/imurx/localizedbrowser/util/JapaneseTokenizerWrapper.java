package io.github.imurx.localizedbrowser.util;

import io.github.imurx.localizedbrowser.LocalizedBrowser;

import java.util.List;

public class JapaneseTokenizerWrapper {
    public final Class<?> aClass;
    public final Object ptr;
    public JapaneseTokenizerWrapper() {
        try {
            this.aClass = Class.forName("com.atilika.kuromoji.unidic.Tokenizer", true, LocalizedBrowser.getInstance().manager.getClassLoader());
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
                    .map(ptr -> new TokenWrapper(ptr, LocalizedBrowser.getInstance().manager.getClassLoader()))
                    .toList();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class TokenWrapper {
        public final Class<?> aClass;
        public final Object ptr;
        protected TokenWrapper(Object ptr, ClassLoader classLoader) {
            try {
                this.aClass = Class.forName("com.atilika.kuromoji.unidic.Token", true, classLoader);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.ptr = ptr;
        }

        public String getPronunciation() {
            try {
                return (String) this.aClass.getMethod("getPronunciation").invoke(this.ptr);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getWrittenForm() {
            try {
                return (String) this.aClass.getMethod("getWrittenForm").invoke(this.ptr);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getSurface() {
            try {
                return (String) this.aClass.getMethod("getSurface").invoke(this.ptr);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
