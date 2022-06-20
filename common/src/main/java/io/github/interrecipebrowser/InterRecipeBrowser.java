package io.github.interrecipebrowser;

import java.text.Normalizer;

public class InterRecipeBrowser {
    public static final String MOD_ID = "interrecipebrowser";

    public static String removeDiacritics(String string) {
        //Maybe string builder should replace the regex part for more performance\
        return Normalizer.normalize(string, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
    }
    
    public static void init() {

    }
}
