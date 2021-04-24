package com.github.sulir.runtimesearch.plugin.config;

import com.github.sulir.runtimesearch.plugin.Messages;
import com.intellij.debugger.ui.PatternFilterEditor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class RuntimeSearchSettingsEditor extends SettingsEditor<RunConfigurationBase<?>> {
    private static final String DEFAULT_INCLUDE = Messages.get("settings.include.empty");

    private final Project project;
    private JPanel mainPanel;
    private JCheckBox enabledCheckBox;
    private PatternFilterEditor includeFilterEditor;
    private TitledSeparator mainSeparator;
    private JLabel includeLabel;

    public RuntimeSearchSettingsEditor(Project project) {
        this.project = project;
    }

    @Override
    protected void resetEditorFrom(@NotNull RunConfigurationBase runConfiguration) {
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(runConfiguration);

        enabledCheckBox.setSelected(settings.isEnabled());
        includeFilterEditor.setFilters(settings.getIncludeFilters());
    }

    @Override
    protected void applyEditorTo(@NotNull RunConfigurationBase runConfiguration) {
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(runConfiguration);

        settings.setEnabled(enabledCheckBox.isSelected());
        settings.setIncludeFilters(includeFilterEditor.getFilters());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return mainPanel;
    }

    private void createUIComponents() {
        includeFilterEditor = new PatternFilterEditor(project);
        includeFilterEditor.getEmptyText().setText(DEFAULT_INCLUDE);
    }
}
