package com.github.sulir.runtimesearch.plugin;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
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

    private Project project;
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
        AnActionEvent actionEvent = AnActionEvent.createFromDataContext(ActionPlaces.UNKNOWN,
                null, DataManager.getInstance().getDataContextFromFocus().getResult());
        ActionManager.getInstance().getAction("Debug").actionPerformed(actionEvent);
    }

    public void sendSearchStringExpression(XDebugSession session) {
        XDebuggerEvaluator evaluator = session.getDebugProcess().getEvaluator();
        String searchString = searchText.isEmpty() ? "null"
                : '"' + StringEscapeUtils.escapeJava(searchText) + '"';
        String expression = String.format(SEND_VALUE, searchString);

        evaluator.evaluate(expression, new XDebuggerEvaluator.XEvaluationCallback() {
            @Override
            public void evaluated(@NotNull XValue result) {
                ApplicationManager.getApplication().invokeLater(session::resume);
            }

            @Override
            public void errorOccurred(@NotNull String errorMessage) { }
        }, null);
    }

    private void sendSearchStringSocket() {
        try (
            Socket client = new Socket(InetAddress.getLoopbackAddress(), PORT);
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
        ) {
            output.writeObject(searchText.isEmpty() ? null : searchText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
