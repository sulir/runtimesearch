package com.github.sulir.runtimesearch.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SearchOptions implements Serializable {
    public static final String PROPERTY_PREFIX = "runtimesearch.";
    private static final long serialVersionUID = 1L;

    private String text = "";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isActive() {
        return !text.isEmpty();
    }

    public Map<String, String> toProperties() {
        Map<String, String> result = new HashMap<>();
        result.put(PROPERTY_PREFIX + "text", text);
        return result;
    }

    public static SearchOptions fromProperties(Properties properties) {
        SearchOptions options = new SearchOptions();

        String text = properties.getProperty(PROPERTY_PREFIX + "text");
        if (text != null)
            options.setText(text);

        return options;
    }
}
