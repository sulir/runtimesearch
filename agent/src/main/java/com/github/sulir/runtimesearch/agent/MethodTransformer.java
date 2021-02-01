package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.analyzer.StackAnalyzer;
import com.github.sulir.runtimesearch.agent.analyzer.StringValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class MethodTransformer {
    private final String className;
    private final MethodNode method;
    private final InsnList instructions;

    public MethodTransformer(String className, MethodNode method) {
        this.className = className;
        this.method = method;
        this.instructions = method.instructions;
    }

    public void transform() throws AnalyzerException {
        StackAnalyzer analyzer = new StackAnalyzer(className, method);
        analyzer.analyze();

        int i = 0;
        for (AbstractInsnNode instruction : instructions) {
            if (instruction.getOpcode() == Opcodes.ALOAD) {
                if (analyzer.getStackTopAfter(i) == StringValue.MAYBE_STRING)
                    instructions.insert(instruction, generateInstrumentation());
            }
            i++;
        }
    }

    private InsnList generateInstrumentation() {
        InsnList instructions = new InsnList();

        instructions.add(new InsnNode(Opcodes.DUP));
        instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/sulir/runtimesearch/runtime/Check",
                "perform", "(Ljava/lang/Object;)V"));

        return instructions;
    }
}
