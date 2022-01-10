package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.SearchOptions;

import java.util.Locale;
import java.util.regex.Pattern;

public class TextSearch {
    private final Pattern pattern;
    private final String needle;
    private final boolean matchCase;

    public TextSearch(SearchOptions options) {
        matchCase = options.isMatchCase();

        if (options.isWholeWords()) {
            int flags = matchCase ? 0 : Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE;
            pattern = Pattern.compile("\\b" + Pattern.quote(options.getText()) + "\\b", flags);
            needle = null;
        } else {
            pattern = null;
            needle = matchCase ? options.getText() : options.getText().toLowerCase(Locale.ROOT);
        }
    }

    public boolean matches(String haystack) {
        if (pattern == null) {
            if (matchCase)
                return haystack.contains(needle);
            else
                return haystack.toLowerCase(Locale.ROOT).contains(needle);
        } else {
            return pattern.matcher(haystack).find();
        }
    }
}
