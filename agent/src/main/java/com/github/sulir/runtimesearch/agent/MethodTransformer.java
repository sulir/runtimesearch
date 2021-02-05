package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.analyzer.StackAnalyzer;
import com.github.sulir.runtimesearch.agent.analyzer.StringValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
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
            switch (instruction.getOpcode()) {
                case LDC:
                case ALOAD:
                case AALOAD:
                case GETSTATIC:
                case GETFIELD:
                    if (analyzer.getStackTopAfter(i) == StringValue.MAYBE_STRING)
                        instructions.insert(instruction, generateInstrumentation());
                    break;
                case INVOKEVIRTUAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                    Type returnType = Type.getType(((MethodInsnNode) instruction).desc).getReturnType();
                    if (new ObjectType(returnType).canBeString())
                        instructions.insert(instruction, generateInstrumentation());
                    break;
                case INVOKEDYNAMIC:
                    returnType = Type.getType(((InvokeDynamicInsnNode) instruction).desc).getReturnType();
                    if (new ObjectType(returnType).canBeString())
                        instructions.insert(instruction, generateInstrumentation());
                    break;
                case INVOKESPECIAL:
                    MethodInsnNode invokespecial = (MethodInsnNode) instruction;

                    if (new ObjectType(Type.getType(invokespecial.desc).getReturnType()).canBeString())
                        instructions.insert(instruction, generateInstrumentation());
                    else if (invokespecial.owner.equals(ObjectType.STRING) && invokespecial.name.equals("<init>"))
                        instructions.insert(instruction, generateInvokestatic());
                    break;
                case NEW:
                    if (analyzer.getStackTopAfter(i) == StringValue.MAYBE_STRING)
                        instructions.insert(instruction, generateDup());
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
