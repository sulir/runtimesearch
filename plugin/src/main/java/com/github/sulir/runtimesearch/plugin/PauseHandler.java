package com.github.sulir.runtimesearch.plugin;

import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;

public class PauseHandler extends AbstractProjectComponent {
    private static final String CLASS = "Lcom/github/sulir/runtimesearch/runtime/Check;";
    private static final String INITIALIZE = CLASS + "#initialize(";
    private static final String FOUND = CLASS + "#perform(";

    public PauseHandler(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        myProject.getMessageBus().connect().subscribe(DebuggerManagerListener.TOPIC, new DebuggerManagerListener() {
            @Override
            public void sessionAttached(DebuggerSession session) {
                XDebugSession xSession = session.getXDebugSession();

                xSession.addSessionListener(new XDebugSessionListener() {
                    @Override
                    public void sessionPaused() {
                        String method = xSession.getCurrentStackFrame().getEqualityObject().toString();

                        if (method.startsWith(INITIALIZE))
                            initializeSearch(xSession);
                        else if (method.startsWith(FOUND))
                            occurrenceFound(xSession);
                    }

                    @Override
                    public void sessionStopped() {
                        RuntimeFindManager.getInstance(myProject).setSearchText("");
                    }
                });
            }
        });
    }

    private void initializeSearch(XDebugSession session) {
        RuntimeFindManager.getInstance(myProject).sendSearchStringExpression(session);
    }

    private void occurrenceFound(XDebugSession session) {
        ApplicationManager.getApplication().invokeLater(session::stepOut);
    }
}
