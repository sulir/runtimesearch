package com.github.sulir.runtimesearch.plugin.config;

import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.util.Key;
import com.intellij.ui.classFilter.ClassFilter;
import com.intellij.util.PatternUtil;
import org.jdom.Element;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RuntimeSearchSettings {
    private static final Key<RuntimeSearchSettings> key = new Key<>(RuntimeSearchSettings.class.getPackage().getName());
    private static final String ENABLED = "enabled";
    private static final String INCLUDE_FILTERS = "includeFilter";
    public static final String PORT = "port";

    private boolean enabled = false;
    private ClassFilter[] includeFilters = ClassFilter.EMPTY_ARRAY;
    private int port = 4321;

    public static RuntimeSearchSettings getOrCreate(RunConfigurationBase<?> runConfiguration) {
        return runConfiguration.putUserDataIfAbsent(key, new RuntimeSearchSettings());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ClassFilter[] getIncludeFilters() {
        return includeFilters;
    }

    public void setIncludeFilters(ClassFilter[] includeFilters) {
        this.includeFilters = includeFilters;
    }

    public String getIncludePattern() {
        return Arrays.stream(includeFilters)
                .filter(ClassFilter::isEnabled)
                .map(ClassFilter::getPattern)
                .map(PatternUtil::convertToRegex)
                .collect(Collectors.joining("|"));
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void writeExternal(Element element) {
        element.setAttribute(ENABLED, String.valueOf(enabled));
        DebuggerUtilsEx.writeFilters(element, INCLUDE_FILTERS, includeFilters);
        element.setAttribute(PORT, String.valueOf(port));
    }

    public void readExternal(Element element) {
        if (element.getAttribute(ENABLED) != null)
            enabled = Boolean.parseBoolean(element.getAttributeValue(ENABLED));

        includeFilters = DebuggerUtilsEx.readFilters(element.getChildren(INCLUDE_FILTERS));

        if (element.getAttribute(PORT) != null)
            port = Integer.parseInt(element.getAttributeValue(PORT));
    }
}
