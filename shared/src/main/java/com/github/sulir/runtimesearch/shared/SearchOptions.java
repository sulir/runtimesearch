package com.github.sulir.runtimesearch.shared;

import java.io.Serializable;

public class SearchOptions implements Serializable {
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
}
