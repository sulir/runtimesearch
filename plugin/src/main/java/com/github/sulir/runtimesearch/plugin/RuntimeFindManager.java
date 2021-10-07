package com.github.sulir.runtimesearch.plugin;

import com.github.sulir.runtimesearch.plugin.breakpoint.RuntimeBreakpointType;
import com.github.sulir.runtimesearch.plugin.config.RuntimeSearchSettings;
import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.github.sulir.runtimesearch.shared.ServerConfig;
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
import com.intellij.openapi.project.Project;
import com.intellij.util.SlowOperations;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RuntimeFindManager {
    private static final String NOTIFICATION_GROUP = "RuntimeSearch";

    private final Project project;
    private RuntimeFindForm form;
    private final SearchOptions options = new SearchOptions();

    public static RuntimeFindManager getInstance(Project project) {
        return project.getService(RuntimeFindManager.class);
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
        if (options.getText().isEmpty()) {
            showForm();
        } else {
            enableBreakpoint();
            XDebugSession session = XDebuggerManager.getInstance(project).getCurrentSession();

            if (session == null) {
                startDebugging();
            } else {
                sendSearchText();
                if (session.isPaused())
                    session.resume();
            }
        }
    }

    public SearchOptions getOptions() {
        return options;
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

    public void sendSearchText() {
        try (
                Socket client = new Socket(InetAddress.getLoopbackAddress(), ServerConfig.PORT);
                ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
                InputStream input = client.getInputStream()
        ) {
            output.writeObject(options);
            @SuppressWarnings("unused") int confirmation = input.read();
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
            SlowOperations.allowSlowOperations(() -> new EditConfigurationsDialog(project).show());
        });

        notification.notify(project);
    }
}
