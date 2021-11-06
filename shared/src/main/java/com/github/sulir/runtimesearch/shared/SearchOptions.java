package com.github.sulir.runtimesearch.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SearchOptions implements Serializable {
    public static final String PROPERTY_PREFIX = "runtimesearch.";
    private static final String TEXT = PROPERTY_PREFIX + "text";
    private static final String MATCH_CASE = PROPERTY_PREFIX + "case";

    private String text = "";
    private boolean matchCase = false;

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

    public Map<String, String> toProperties() {
        Map<String, String> result = new HashMap<>();
        result.put(TEXT, text);
        result.put(MATCH_CASE, String.valueOf(matchCase));
        return result;
    }

    public static SearchOptions fromProperties(Properties properties) {
        SearchOptions options = new SearchOptions();

        if (properties.containsKey(TEXT))
            options.setText(properties.getProperty(TEXT));

        if (properties.containsKey(MATCH_CASE))
            options.setMatchCase(Boolean.parseBoolean(properties.getProperty(MATCH_CASE)));

        return options;
    }

    public static void clearSystemProperties() {
        for (String name : System.getProperties().stringPropertyNames()) {
            if (name.startsWith(SearchOptions.PROPERTY_PREFIX))
                System.clearProperty(name);
        }
    }
}
