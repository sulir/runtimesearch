package com.github.sulir.runtimesearch.plugin;

import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;

public class RuntimeFindForm {
    private final Project project;
    private JBPopup popup;
    private JPanel panel;
    private JTextField searchField;
    private JButton findButton;

    public RuntimeFindForm(Project project) {
        this.project = project;
        searchField.addActionListener(e -> findButtonPressed());
        findButton.addActionListener(e -> findButtonPressed());
    }

    public void show() {
        if (popup == null || popup.isDisposed()) {
            ComponentPopupBuilder builder = JBPopupFactory.getInstance()
                    .createComponentPopupBuilder(panel, searchField);
            IconButton cancelButton = new IconButton(Messages.get("find.close"), AllIcons.Actions.Close,
                    AllIcons.Actions.CloseHovered);
            popup = builder.setTitle(Messages.get("find.title"))
                    .setMovable(true)
                    .setRequestFocus(true)
                    .setCancelOnClickOutside(false)
                    .setCancelButton(cancelButton)
                    .setCancelOnWindowDeactivation(false)
                    .createPopup();
        }

        if (!popup.isVisible()) {
            load();
            popup.showCenteredInCurrentWindow(project);
        }

        if (!popup.isFocused()) {
            searchField.requestFocus();
        }
    }

    public void hide() {
        popup.cancel();
    }

    private void load() {
        SearchOptions options = RuntimeFindManager.getInstance(project).getOptions();
        searchField.setText(options.getText());
    }

    private void save() {
        SearchOptions options = RuntimeFindManager.getInstance(project).getOptions();
        options.setText(searchField.getText());
    }

    private void findButtonPressed() {
        save();
        RuntimeFindManager.getInstance(project).findNext();
    }
}
