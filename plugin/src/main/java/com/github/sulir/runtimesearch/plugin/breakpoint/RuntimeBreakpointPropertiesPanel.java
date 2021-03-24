package com.github.sulir.runtimesearch.plugin.breakpoint;

import com.github.sulir.runtimesearch.plugin.Messages;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.debugger.breakpoints.properties.JavaExceptionBreakpointProperties;

import javax.swing.*;

public class RuntimeBreakpointPropertiesPanel
        extends XBreakpointCustomPropertiesPanel<XBreakpoint<JavaExceptionBreakpointProperties>> {
    @Override
    public @NotNull JComponent getComponent() {
        return new JLabel("<html>" + Messages.get("breakpoint.properties.info") + "</html>" );
    }

    @Override
    public void loadFrom(@NotNull XBreakpoint breakpoint) { }

    @Override
    public void saveTo(@NotNull XBreakpoint breakpoint) { }
}
