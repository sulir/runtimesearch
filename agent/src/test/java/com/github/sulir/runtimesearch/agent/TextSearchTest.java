package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.SearchOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextSearchTest {

    @Test
    void standardSearchMatches() {
        TextSearch lowercase = new TextSearch(new SearchOptions("e", false, false));
        assertTrue(lowercase.matches("text"));

        TextSearch uppercase = new TextSearch(new SearchOptions("E", false, false));
        assertTrue(uppercase.matches("text"));
    }

    @Test
    void standardSearchDoesNotMatch() {
        TextSearch nonPresent = new TextSearch(new SearchOptions("n", false, false));
        assertFalse(nonPresent.matches("text"));
    }

    @Test
    void matchCaseSearchMatches() {
        TextSearch lowercase = new TextSearch(new SearchOptions("e", true, false));
        assertTrue(lowercase.matches("text"));
    }

    @Test
    void matchCaseSearchDoesNotMatch() {
        TextSearch uppercase = new TextSearch(new SearchOptions("E", true, false));
        assertFalse(uppercase.matches("text"));

        TextSearch nonPresent = new TextSearch(new SearchOptions("q", true, false));
        assertFalse(nonPresent.matches("text"));
    }

    @Test
    void wholeWordsSearchMatches() {
        TextSearch word = new TextSearch(new SearchOptions("text", false, true));
        assertTrue(word.matches("some text 123"));

        TextSearch wordUppercase = new TextSearch(new SearchOptions("TEXT", false, true));
        assertTrue(wordUppercase.matches("some text 123"));
    }

    @Test
    void wholeWordsSearchDoesNotMatch() {
        TextSearch letter = new TextSearch(new SearchOptions("e", false, true));
        assertFalse(letter.matches("text"));
    }

    @Test
    void wholeWordsMatchCaseSearchMatches() {
        TextSearch lowercaseWord = new TextSearch(new SearchOptions("text", true, true));
        assertTrue(lowercaseWord.matches("some text 123"));
    }

    @Test
    void wholeWordsMatchCaseSearchDoesNotMatch() {
        TextSearch letter = new TextSearch(new SearchOptions("e", true, true));
        assertFalse(letter.matches("text"));

        TextSearch uppercase = new TextSearch(new SearchOptions("TEXT", true, true));
        assertFalse(uppercase.matches("some text 123"));
    }
}