package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.BreakpointError;
import com.github.sulir.runtimesearch.shared.SearchOptions;

public class Check {
    private static boolean active;
    private static SearchOptions options;

    public static void setOptions(SearchOptions options) {
        Check.options = options;
        active = options.isActive();
    }

    public static void initialize() {
        SearchOptions options = SearchOptions.fromProperties(System.getProperties());
        setOptions(options);
    }

    public static void perform(Object object) {
        if (!active)
            return;

        if (object instanceof String) {
            String string = (String) object;
            if (string.contains(options.getText())) {
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
