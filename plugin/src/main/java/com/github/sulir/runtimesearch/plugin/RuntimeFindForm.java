package com.github.sulir.runtimesearch.plugin;

import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import javax.swing.*;
import java.awt.event.ItemEvent;

public class RuntimeFindForm {
    private final Project project;
    private JBPopup popup;
    private JPanel panel;
    private JTextField searchField;
    private JButton findButton;
    private JCheckBox matchCaseCheckBox;
    private JCheckBox wholeWordsCheckBox;
    private JCheckBox regexCheckBox;

    public RuntimeFindForm(Project project) {
        this.project = project;
        searchField.addActionListener(e -> findButtonPressed());
        findButton.addActionListener(e -> findButtonPressed());

        regexCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                wholeWordsCheckBox.setSelected(false);
                wholeWordsCheckBox.setEnabled(false);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                wholeWordsCheckBox.setEnabled(true);
            }
        });
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
        matchCaseCheckBox.setSelected(options.isMatchCase());
        wholeWordsCheckBox.setSelected(options.isWholeWords());
        regexCheckBox.setSelected(options.isRegex());
    }

    private void save() {
        SearchOptions options = RuntimeFindManager.getInstance(project).getOptions();
        options.setText(searchField.getText());
        options.setMatchCase(matchCaseCheckBox.isSelected());
        options.setWholeWords(wholeWordsCheckBox.isSelected());
        options.setRegex(regexCheckBox.isSelected());
    }

    private void findButtonPressed() {
        save();
        RuntimeFindManager.getInstance(project).findNext();
    }
}
