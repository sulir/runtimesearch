package com.github.sulir.runtimesearch.plugin.config;

import com.intellij.ui.classFilter.ClassFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuntimeSearchSettingsTest {
    RuntimeSearchSettings settings;

    @BeforeEach
    void setUp() {
        settings = new RuntimeSearchSettings();
    }

    @Test
    void emptyIncludeFilterProducesEmptyPattern() {
        settings.setIncludeFilters(ClassFilter.EMPTY_ARRAY);
        assertEquals("", settings.getIncludePattern());
    }

    @Test
    void onePackageFilterWithWildcardProducesRegexPattern() {
        settings.setIncludeFilters(new ClassFilter[] { new ClassFilter("com.package.*") });
        assertEquals("com\\.package\\..*", settings.getIncludePattern());
    }

    @Test
    void twoClassFilterProducesRegexWithPipe() {
        settings.setIncludeFilters(new ClassFilter[] { new ClassFilter("one.One"), new ClassFilter("two.Two") });
        assertEquals("one\\.One|two\\.Two", settings.getIncludePattern());
    }
}