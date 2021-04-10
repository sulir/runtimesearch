package com.github.sulir.runtimesearch.plugin;

import com.github.sulir.runtimesearch.plugin.breakpoint.RuntimeBreakpointType;
import com.github.sulir.runtimesearch.plugin.config.RuntimeSearchSettings;
import com.intellij.debugger.engine.JavaValue;
import com.intellij.execution.Executor;
import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.impl.EditConfigurationsDialog;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XValue;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RuntimeFindManager {
    public static final int PORT = 4321;
    private static final String SEND_VALUE = "Class.forName(\"com.github.sulir.runtimesearch" +
            ".runtime.Check\").getDeclaredField(\"searchValue\").set(null, %s)";
    private static final String NOTIFICATION_GROUP = "RuntimeSearch";

    private final Project project;
    private RuntimeFindForm form;
    private String searchText = "";

    public static RuntimeFindManager getInstance(Project project) {
        return ServiceManager.getService(project, RuntimeFindManager.class);
    }

    public RuntimeFindManager(Project project) {
        this.project = project;
    }

    public void showForm() {
        if (form == null)
            form = new RuntimeFindForm(project);

        form.show();
    }

    public void findNext() {
        if (searchText.isEmpty()) {
            showForm();
        } else {
            enableBreakpoint();
            XDebugSession session = XDebuggerManager.getInstance(project).getCurrentSession();

            if (session == null) {
                startDebugging();
            } else {
                if (session.isPaused())
                    sendSearchStringExpression(session);
                else
                    sendSearchStringSocket();
            }
        }
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void startDebugging() {
        RunnerAndConfigurationSettings selected = RunManager.getInstance(project).getSelectedConfiguration();
        if (selected == null || !(selected.getConfiguration() instanceof RunConfigurationBase)) {
            Notifications.Bus.notify(new Notification(NOTIFICATION_GROUP, Messages.get("error.no.config.title"),
                    Messages.get("error.no.config.content"), NotificationType.ERROR));
            return;
        }

        RunConfigurationBase<?> configuration = (RunConfigurationBase<?>) selected.getConfiguration();
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(configuration);
        if (!settings.isEnabled()) {
            offerToEnablePlugin();
            return;
        }

        Executor debugExecutor = DefaultDebugExecutor.getDebugExecutorInstance();
        ProgramRunnerUtil.executeConfiguration(selected, debugExecutor);
    }

    public void sendSearchStringExpression(XDebugSession session) {
        XDebuggerEvaluator evaluator = session.getDebugProcess().getEvaluator();
        assert evaluator != null;
        String searchString = searchText.isEmpty() ? "null"
                : '"' + StringEscapeUtils.escapeJava(searchText) + '"';
        String expression = String.format(SEND_VALUE, searchString);

        evaluator.evaluate(expression, new XDebuggerEvaluator.XEvaluationCallback() {
            @Override
            public void evaluated(@NotNull XValue result) {
                String resultType = ((JavaValue) result).getTag();
                if (resultType != null && resultType.equals("java.lang.ClassNotFoundException")) {
                    offerToEnablePlugin();
                } else {
                    ApplicationManager.getApplication().invokeLater(session::resume);
                }
            }

            @Override
            public void errorOccurred(@NotNull String errorMessage) {
                new Notification(NOTIFICATION_GROUP, Messages.get("error.paused.title"),
                        Messages.get("error.paused.content"), NotificationType.ERROR).notify(project);
            }
        }, null);
    }

    private void sendSearchStringSocket() {
        try (
            Socket client = new Socket(InetAddress.getLoopbackAddress(), PORT);
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream())
        ) {
            output.writeObject(searchText.isEmpty() ? null : searchText);
        } catch (IOException e) {
            offerToEnablePlugin();
        }
    }

    private void enableBreakpoint() {
        RuntimeBreakpointType type = XDebuggerUtil.getInstance().findBreakpointType(RuntimeBreakpointType.class);
        XBreakpointManager manager = XDebuggerManager.getInstance(project).getBreakpointManager();
        XBreakpoint<?> breakpoint = manager.getDefaultBreakpoints(type).iterator().next();
        breakpoint.setEnabled(true);
    }

    private void offerToEnablePlugin() {
        Notification notification = new Notification(NOTIFICATION_GROUP, Messages.get("error.disabled.title"),
                Messages.get("error.disabled.content"), NotificationType.WARNING);

        notification.setListener((n, event) -> {
            if (form != null)
                form.hide();
            new EditConfigurationsDialog(project).show();
        });

        notification.notify(project);
    }
}
