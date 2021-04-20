package com.github.sulir.runtimesearch.plugin.breakpoint;

import com.github.sulir.runtimesearch.shared.BreakpointError;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.ExceptionBreakpoint;
import com.intellij.debugger.ui.breakpoints.JavaBreakpointType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointType;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointCustomPropertiesPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.java.debugger.breakpoints.properties.JavaExceptionBreakpointProperties;

import javax.swing.*;
import java.util.EnumSet;

public class RuntimeBreakpointType
        extends XBreakpointType<XBreakpoint<JavaExceptionBreakpointProperties>, JavaExceptionBreakpointProperties>
        implements JavaBreakpointType<JavaExceptionBreakpointProperties> {
    private static final String BREAKPOINT_CLASS = BreakpointError.class.getName();

    protected RuntimeBreakpointType() {
        super("java-runtime", "Runtime Breakpoints", true);
    }

    @Override
    public @NotNull Icon getEnabledIcon() {
        return AllIcons.Debugger.Db_exception_breakpoint;
    }

    @Override
    public @NotNull Icon getDisabledIcon() {
        return AllIcons.Debugger.Db_disabled_exception_breakpoint;
    }

    @Override
    public String getDisplayText(XBreakpoint breakpoint) {
        return "RuntimeSearch";
    }

    public boolean isAddBreakpointButtonVisible() {
        return false;
    }

    @Override
    public EnumSet<StandardPanels> getVisibleStandardPanels() {
        return EnumSet.of(StandardPanels.SUSPEND_POLICY);
    }

    @Override
    public XBreakpointCustomPropertiesPanel<XBreakpoint<JavaExceptionBreakpointProperties>>
    createCustomTopPropertiesPanel(@NotNull Project project) {
        return new RuntimeBreakpointPropertiesPanel();
    }

    @Override
    public JavaExceptionBreakpointProperties createProperties() {
        return new JavaExceptionBreakpointProperties(BREAKPOINT_CLASS, "");
    }

    @NotNull
    @Override
    public Breakpoint<JavaExceptionBreakpointProperties> createJavaBreakpoint(Project project,
            XBreakpoint<JavaExceptionBreakpointProperties> breakpoint) {
        return new ExceptionBreakpoint(project, breakpoint);
    }

    @Override
    public XBreakpoint<JavaExceptionBreakpointProperties> createDefaultBreakpoint(
            @NotNull XBreakpointCreator<JavaExceptionBreakpointProperties> creator) {
        XBreakpoint<JavaExceptionBreakpointProperties> breakpoint = creator.createBreakpoint(createProperties());

        breakpoint.setEnabled(true);
        return breakpoint;
    }
}
