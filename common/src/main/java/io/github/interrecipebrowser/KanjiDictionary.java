package io.github.interrecipebrowser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class KanjiDictionary {
    private final Map<String, KanjiReading> dictionary;

    KanjiDictionary(Reader file) {
        Type type = new TypeToken<HashMap<String, KanjiReading>>(){}.getType();
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(RecordTypeAdapterFactory.DEFAULT)
                .create();
        dictionary = gson.fromJson(new JsonReader(file), type);
    }

    public boolean hasReading(String kanji) {
        return this.dictionary.containsKey(kanji);
    }

    public Stream<String> getReadings(String kanji) {
        var reading = this.dictionary.get(kanji);
        if(reading == null) return null;
        return Stream.concat(reading.kunyomi.stream(), reading.onyomi.stream());
    }

    public List<String> getKunyomiReadings(String kanji) {
        var reading = this.dictionary.get(kanji);
        if(reading == null) return null;
        return reading.kunyomi;
    }

    public List<String> getOnyomiReadings(String kanji) {
        var reading = this.dictionary.get(kanji);
        if(reading == null) return null;
        return reading.onyomi;
    }

    public record KanjiReading(List<String> kunyomi, List<String> onyomi) {}
}
