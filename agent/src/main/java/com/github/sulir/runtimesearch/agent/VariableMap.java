package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LocalVariableNode;

import java.util.*;

public class VariableMap {
    private final Map<Label, Set<Integer>> scopeStarts = new HashMap<>();
    private final Map<Label, Set<Integer>> scopeEnds = new HashMap<>();
    private final Set<Integer> searchableVariables = new HashSet<>();

    public void addVariables(List<LocalVariableNode> variables) {
        for (LocalVariableNode variable : variables) {
            if (Descriptor.canBeString(variable.desc)) {
                scopeStarts.computeIfAbsent(variable.start.getLabel(), k -> new HashSet<>()).add(variable.index);
                scopeEnds.computeIfAbsent(variable.end.getLabel(), k -> new HashSet<>()).add(variable.index);
            }
        }
    }

    public void updateScope(Label label) {
        searchableVariables.removeAll(scopeEnds.getOrDefault(label, Collections.emptySet()));
        searchableVariables.addAll(scopeStarts.getOrDefault(label, Collections.emptySet()));
    }

    public boolean isSearchable(int variableIndex) {
        return searchableVariables.contains(variableIndex);
    }
}
