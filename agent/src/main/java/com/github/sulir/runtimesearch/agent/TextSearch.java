package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.SearchOptions;

import java.util.Locale;

public class TextSearch {
    private final String needle;
    private final boolean matchCase;

    public TextSearch(SearchOptions options) {
        matchCase = options.isMatchCase();

        if (matchCase)
            needle = options.getText();
        else
            needle = options.getText().toLowerCase(Locale.ROOT);
    }

    public boolean matches(String haystack) {
        if (matchCase)
            return haystack.contains(needle);
        else
            return haystack.toLowerCase(Locale.ROOT).contains(needle);
    }
}
