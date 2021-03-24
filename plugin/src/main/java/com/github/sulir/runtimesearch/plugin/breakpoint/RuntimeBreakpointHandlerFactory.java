package com.github.sulir.runtimesearch.plugin.breakpoint;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaBreakpointHandler;
import com.intellij.debugger.engine.JavaBreakpointHandlerFactory;

public class RuntimeBreakpointHandlerFactory implements JavaBreakpointHandlerFactory {
    @Override
    public JavaBreakpointHandler createHandler(DebugProcessImpl process) {
        return new JavaBreakpointHandler(RuntimeBreakpointType.class, process) { };
    }
}
