package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.BreakpointError;
import com.github.sulir.runtimesearch.shared.SearchOptions;

public class Check {
    private static TextSearch search;
    private static boolean active;

    public static void initialize() {
        SearchOptions options = SearchOptions.fromProperties(System.getProperties());
        setOptions(options);

        SearchOptions.clearSystemProperties();
    }

    public static void setOptions(SearchOptions options) {
        Check.search = new TextSearch(options);
        active = !options.getText().isEmpty();
    }

    public static void perform(Object object) {
        if (!active)
            return;

        if (object instanceof String) {
            String string = (String) object;

            if (search.matches(string)) {
                try {
                    active = false;
                    throw new BreakpointError();
                } catch (BreakpointError e) {
                    // exception thrown to trigger a breakpoint in the IDE
                }
            }
        }
    }
}
