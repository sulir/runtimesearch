package com.github.sulir.runtimesearch.plugin;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;

public class PauseHandler implements DebuggerManagerListener {
    private static final String CLASS = "Lcom/github/sulir/runtimesearch/runtime/Check;";
    private static final String INITIALIZE = CLASS + "#initialize(";
    private static final String FOUND = CLASS + "#perform(";

    private final Project project;

    public PauseHandler() {
        this.project = null;
    }

    public PauseHandler(Project project) {
        this.project = project;
    }

    @Override
    public void sessionAttached(DebuggerSession session) {
        XDebugSession xSession = session.getXDebugSession();
        if (project == null || xSession == null)
            return;

        xSession.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
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
        RuntimeFindManager.getInstance(project).sendSearchStringExpression(session);
    }

    private void occurrenceFound(XDebugSession session) {
        ApplicationManager.getApplication().invokeLater(session::stepOut);
    }
}
