package com.github.sulir.runtimesearch.shared;

public class Check {
    private static boolean active;
    private static SearchOptions options;

    public static void setOptions(SearchOptions options) {
        Check.options = options;
        active = options.isActive();
    }

    public static void initialize() {
        try {
            throw new BreakpointError();
        } catch (BreakpointError e) {
            // exception thrown to trigger a breakpoint in the IDE
        }
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
