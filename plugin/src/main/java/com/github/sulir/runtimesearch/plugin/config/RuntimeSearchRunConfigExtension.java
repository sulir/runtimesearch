package com.github.sulir.runtimesearch.plugin.config;

import com.github.sulir.runtimesearch.plugin.RuntimeFindManager;
import com.github.sulir.runtimesearch.shared.SearchOptions;
import com.github.sulir.runtimesearch.shared.SharedConfig;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.DebuggingRunnerData;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.options.SettingsEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

public class RuntimeSearchRunConfigExtension extends RunConfigurationExtension {
    @Override
    public void updateJavaParameters(@NotNull RunConfigurationBase configuration, @NotNull JavaParameters params,
                                     RunnerSettings runnerSettings) {
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(configuration);

        if (settings.isEnabled() && runnerSettings instanceof DebuggingRunnerData) {
            PluginId pluginId = PluginManager.getPluginByClassName(RuntimeFindManager.class.getName());
            Path pluginPath = Objects.requireNonNull(PluginManagerCore.getPlugin(pluginId)).getPluginPath();
            String agentPath = pluginPath.resolve("lib").resolve("runtimesearch-agent.jar").toString();

            params.getVMParametersList().add("-javaagent:" + agentPath + "=" + settings.getPort());
            params.getVMParametersList().addProperty(SharedConfig.INCLUDE_PROPERTY, settings.getIncludePattern());

            SearchOptions options = RuntimeFindManager.getInstance(configuration.getProject()).getOptions();
            options.toProperties().forEach((key, value) -> params.getVMParametersList().addProperty(key, value));
        }
    }

    @Override
    protected String getEditorTitle() {
        return "RuntimeSearch";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected SettingsEditor<RunConfigurationBase<?>> createEditor(@NotNull RunConfigurationBase configuration) {
        return new RuntimeSearchSettingsEditor(configuration.getProject());
    }

    @Override
    protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) {
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(runConfiguration);
        settings.readExternal(element);
    }

    @Override
    protected void writeExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) {
        RuntimeSearchSettings settings = RuntimeSearchSettings.getOrCreate(runConfiguration);
        settings.writeExternal(element);
    }

    @Override
    public boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
        return true;
    }
}
