package com.github.sulir.runtimesearch.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class RuntimeFindAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        RuntimeFindManager.getInstance(event.getProject()).showForm();
    }
}
