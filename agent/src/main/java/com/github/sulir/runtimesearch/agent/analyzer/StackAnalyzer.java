package com.github.sulir.runtimesearch.agent.analyzer;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class StackAnalyzer {
    private final String className;
    private final MethodNode method;
    private StringValue[] stackTop;

    public StackAnalyzer(String className, MethodNode method) {
        this.className = className;
        this.method = method;
    }

    public void analyze() throws AnalyzerException {
        Analyzer<StringValue> analyzer = new Analyzer<>(new IsStringInterpreter());
        Frame<StringValue>[] frames = analyzer.analyze(className, method);

        stackTop = new StringValue[frames.length];
        for (int i = 0; i < frames.length; i++) {
            if (frames[i] != null)
                stackTop[i] = frames[i].getStackSize() > 0 ? frames[i].getStack(frames[i].getStackSize() - 1) : null;
        }
    }

    public StringValue getStackTopAfter(int instruction) {
        if (instruction < stackTop.length - 1)
            return stackTop[instruction + 1];
        else
            return null;
    }
}
