package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.analyzer.StackAnalyzer;
import com.github.sulir.runtimesearch.agent.analyzer.StringValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class MethodTransformer implements Opcodes {
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
            if (analyzer.getStackTopAfter(i) == StringValue.MAYBE_STRING) {
                switch (instruction.getOpcode()) {
                    case LDC:
                    case ALOAD:
                    case AALOAD:
                    case GETSTATIC:
                    case GETFIELD:
                    case INVOKEVIRTUAL:
                    case INVOKESPECIAL:
                    case INVOKESTATIC:
                    case INVOKEINTERFACE:
                    case INVOKEDYNAMIC:
                        instructions.insert(instruction, generateInstrumentation());
                        break;
                    case NEW:
                        instructions.insert(instruction, generateDup());
                }
            } else if (instruction.getOpcode() == INVOKESPECIAL) {
                MethodInsnNode invokespecial = (MethodInsnNode) instruction;

                if (invokespecial.owner.equals("java/lang/String") && invokespecial.name.equals("<init>"))
                    instructions.insert(instruction, generateInvokestatic());
            }

            i++;
        }
    }

    private AbstractInsnNode generateDup() {
        return new InsnNode(Opcodes.DUP);
    }

    private AbstractInsnNode generateInvokestatic() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, "com/github/sulir/runtimesearch/runtime/Check",
                "perform", "(Ljava/lang/Object;)V");
    }

    private InsnList generateInstrumentation() {
        InsnList instructions = new InsnList();

        instructions.add(generateDup());
        instructions.add(generateInvokestatic());

        return instructions;
    }
}
