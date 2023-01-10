package com.github.sulir.runtimesearch.plugin.actions;

import com.github.sulir.runtimesearch.plugin.RuntimeFindManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RuntimeFindNextAction extends MenuAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getProject() != null)
            RuntimeFindManager.getInstance(e.getProject()).findNext();
    }
}
