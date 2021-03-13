package com.github.sulir.runtimesearch.plugin;

import java.util.ResourceBundle;

public class Messages {
    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    public static String get(String key) {
        return bundle.getString(key);
    }
}
