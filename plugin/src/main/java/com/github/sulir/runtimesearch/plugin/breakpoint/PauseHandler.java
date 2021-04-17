package com.github.sulir.runtimesearch.plugin.breakpoint;

import com.github.sulir.runtimesearch.plugin.RuntimeFindManager;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

import java.util.Optional;

public class PauseHandler implements DebuggerManagerListener {
    private static final String CLASS = "Lcom/github/sulir/runtimesearch/runtime/Check;";
    private static final String INITIALIZE = CLASS + "#initialize(";
    private static final String FOUND = CLASS + "#perform(";

    private final Project project;

    public PauseHandler(Project project) {
        this.project = project;
    }

    @Override
    public void sessionAttached(DebuggerSession session) {
        XDebugSession xSession = session.getXDebugSession();
        if (xSession == null)
            return;

        xSession.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                VirtualMachine vm = session.getProcess().getVirtualMachineProxy().getVirtualMachine();
                Optional<ThreadReference> thread = vm.allThreads().stream().filter(t ->
                        t.name().equals("RuntimeSearch")).findFirst();
                thread.ifPresent(ThreadReference::resume);

                XStackFrame stackFrame = xSession.getCurrentStackFrame();
                if (stackFrame == null)
                    return;

                String method = String.valueOf(stackFrame.getEqualityObject());

                if (method.startsWith(INITIALIZE))
                    initializeSearch(xSession);
                else if (method.startsWith(FOUND))
                    occurrenceFound(xSession);
            }

            @Override
            public void sessionStopped() {
                RuntimeFindManager.getInstance(project).setSearchText("");
            }
        });
    }

    private void initializeSearch(XDebugSession session) {
        RuntimeFindManager.getInstance(project).sendSearchText();
        ApplicationManager.getApplication().invokeLater(session::resume);
    }

    private void occurrenceFound(XDebugSession session) {
        ApplicationManager.getApplication().invokeLater(session::stepOut);
    }
}
