package com.github.sulir.runtimesearch.plugin.breakpoint;

import com.github.sulir.runtimesearch.plugin.RuntimeFindManager;
import com.github.sulir.runtimesearch.shared.BreakpointError;
import com.github.sulir.runtimesearch.shared.ServerConfig;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.impl.DebuggerManagerListener;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.ExceptionEvent;

import java.util.Optional;

public class PauseHandler implements DebuggerManagerListener {
    private static final String BREAKPOINT_CLASS = BreakpointError.class.getName();

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
                resumeServerThread(session);
                stepOutIfFound(session);
            }

            @Override
            public void sessionStopped() {
                RuntimeFindManager.getInstance(project).getOptions().setText("");
            }
        });
    }

    private void resumeServerThread(DebuggerSession session) {
        VirtualMachine vm = session.getProcess().getVirtualMachineProxy().getVirtualMachine();
        Optional<ThreadReference> thread = vm.allThreads().stream().filter(t ->
                t.name().equals(ServerConfig.THREAD_NAME)).findFirst();
        thread.ifPresent(ThreadReference::resume);
    }

    private void stepOutIfFound(DebuggerSession session) {
        SuspendContextImpl context = session.getContextManager().getContext().getSuspendContext();
        if (context == null || context.getEventSet() == null)
            return;

        boolean pausedByOurBreakpoint = context.getEventSet().stream().anyMatch(event ->
                event instanceof ExceptionEvent
                && ((ExceptionEvent) event).exception().type().name().equals(BREAKPOINT_CLASS));

        if (pausedByOurBreakpoint)
            ApplicationManager.getApplication().invokeLater(session::stepOut);
    }
}
