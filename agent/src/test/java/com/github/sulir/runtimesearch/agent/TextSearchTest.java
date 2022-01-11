package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.shared.SearchOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextSearchTest {
    @Test
    void standardSearchMatches() {
        TextSearch lowercase = new TextSearch(new SearchOptions("e", false, false, false));
        assertTrue(lowercase.matches("text"));

        TextSearch uppercase = new TextSearch(new SearchOptions("E", false, false, false));
        assertTrue(uppercase.matches("text"));
    }

    @Test
    void standardSearchDoesNotMatch() {
        TextSearch nonPresent = new TextSearch(new SearchOptions("n", false, false, false));
        assertFalse(nonPresent.matches("text"));
    }

    @Test
    void matchCaseSearchMatches() {
        TextSearch lowercase = new TextSearch(new SearchOptions("e", true, false, false));
        assertTrue(lowercase.matches("text"));
    }

    @Test
    void matchCaseSearchDoesNotMatch() {
        TextSearch uppercase = new TextSearch(new SearchOptions("E", true, false, false));
        assertFalse(uppercase.matches("text"));

        TextSearch nonPresent = new TextSearch(new SearchOptions("q", true, false, false));
        assertFalse(nonPresent.matches("text"));
    }

    @Test
    void wholeWordsSearchMatches() {
        TextSearch word = new TextSearch(new SearchOptions("text", false, true, false));
        assertTrue(word.matches("some text 123"));

        TextSearch wordUppercase = new TextSearch(new SearchOptions("TEXT", false, true, false));
        assertTrue(wordUppercase.matches("some text 123"));
    }

    @Test
    void wholeWordsSearchDoesNotMatch() {
        TextSearch letter = new TextSearch(new SearchOptions("e", false, true, false));
        assertFalse(letter.matches("text"));
    }

    @Test
    void wholeWordsMatchCaseSearchMatches() {
        TextSearch lowercaseWord = new TextSearch(new SearchOptions("text", true, true, false));
        assertTrue(lowercaseWord.matches("some text 123"));
    }

    @Test
    void wholeWordsMatchCaseSearchDoesNotMatch() {
        TextSearch letter = new TextSearch(new SearchOptions("e", true, true, false));
        assertFalse(letter.matches("text"));

        TextSearch uppercase = new TextSearch(new SearchOptions("TEXT", true, true, false));
        assertFalse(uppercase.matches("some text 123"));
    }

    @Test
    void regexSearchMatches() {
        TextSearch regex = new TextSearch(new SearchOptions("t.{2}t", false, false, true));
        assertTrue(regex.matches("123text123"));

        TextSearch uppercase = new TextSearch(new SearchOptions("T.{2}T", false, false, true));
        assertTrue(uppercase.matches("123text123"));
    }

    @Test
    void regexSearchDoesNotMatch() {
        TextSearch regex = new TextSearch(new SearchOptions("t.{3}t", false, false, true));
        assertFalse(regex.matches("123text123"));
    }

    @Test
    void regexMatchCaseSearchMatches() {
        TextSearch regex = new TextSearch(new SearchOptions("t.{2}t", true, false, true));
        assertTrue(regex.matches("123text123"));
    }

    @Test
    void regexMatchCaseSearchDoesNotMatch() {
        TextSearch regex = new TextSearch(new SearchOptions("t.{3}t", true, false, true));
        assertFalse(regex.matches("123text123"));

        TextSearch uppercase = new TextSearch(new SearchOptions("T.{2}T", true, false, true));
        assertFalse(uppercase.matches("123text123"));
    }
}