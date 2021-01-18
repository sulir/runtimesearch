package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class StackAnalyzer {
    private final MethodNode method;
    private BasicValue[] stackTop;

    public StackAnalyzer(MethodNode method) {
        this.method = method;
    }

    public void analyze() throws AnalyzerException {
        Analyzer<BasicValue> analyzer = new Analyzer<>(new BasicInterpreter());
        Frame<BasicValue>[] frames = analyzer.analyze("java/lang/Object", method);

        stackTop = new BasicValue[frames.length];
        for (int i = 0; i < frames.length; i++)
            stackTop[i] = frames[i].getStackSize() > 0 ? frames[i].getStack(frames[i].getStackSize() - 1) : null;
    }

    public BasicValue getStackTopAfter(int instruction) {
        if (instruction < stackTop.length - 1)
            return stackTop[instruction + 1];
        else
            return null;
    }
}
