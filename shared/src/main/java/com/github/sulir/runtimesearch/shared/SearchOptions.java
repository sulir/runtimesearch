package com.github.sulir.runtimesearch.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SearchOptions implements Serializable {
    public static final String PROPERTY_PREFIX = "runtimesearch.";
    private static final String TEXT = PROPERTY_PREFIX + "text";
    private static final String MATCH_CASE = PROPERTY_PREFIX + "case";
    private static final String WHOLE_WORDS = PROPERTY_PREFIX + "words";
    public static final String REGEX = PROPERTY_PREFIX + "regex";

    private String text;
    private boolean matchCase;
    private boolean wholeWords;
    private boolean regex;

    public SearchOptions() {
        this("", false, false, false);
    }

    public SearchOptions(String text, boolean matchCase, boolean wholeWords, boolean regex) {
        this.text = text;
        this.matchCase = matchCase;
        this.wholeWords = wholeWords;
        this.regex = regex;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    public boolean isWholeWords() {
        return wholeWords;
    }

    public void setWholeWords(boolean wholeWords) {
        this.wholeWords = wholeWords;
    }

    public boolean isRegex() {
        return regex;
    }

    public void setRegex(boolean regex) {
        this.regex = regex;
    }

    public Map<String, String> toProperties() {
        Map<String, String> result = new HashMap<>();
        result.put(TEXT, text);
        result.put(MATCH_CASE, String.valueOf(matchCase));
        result.put(WHOLE_WORDS, String.valueOf(wholeWords));
        result.put(REGEX, String.valueOf(regex));
        return result;
    }

    public static SearchOptions fromProperties(Properties properties) {
        SearchOptions options = new SearchOptions();

        if (properties.containsKey(TEXT))
            options.setText(properties.getProperty(TEXT));

        if (properties.containsKey(MATCH_CASE))
            options.setMatchCase(Boolean.parseBoolean(properties.getProperty(MATCH_CASE)));

        if (properties.containsKey(WHOLE_WORDS))
            options.setWholeWords(Boolean.parseBoolean(properties.getProperty(WHOLE_WORDS)));

        if (properties.containsKey(REGEX))
            options.setRegex(Boolean.parseBoolean(properties.getProperty(REGEX)));

        return options;
    }

    public static void clearSystemProperties() {
        for (String name : System.getProperties().stringPropertyNames()) {
            if (name.startsWith(SearchOptions.PROPERTY_PREFIX))
                System.clearProperty(name);
        }
    }
}
