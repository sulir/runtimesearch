package com.github.sulir.runtimesearch.runtime;

import java.io.Serializable;

public class SearchOptions implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int PORT = 4321;
    public static final int CONFIRMATION = 0;

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
}
