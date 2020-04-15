package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class MethodTransformer {
    private final MethodNode method;
    private final InsnList instructions;

    public MethodTransformer(MethodNode method) {
        this.method = method;
        this.instructions = method.instructions;
    }

    public void transform() {
        VariableMap variableMap = new VariableMap();
        variableMap.addVariables(method.localVariables);

        for (AbstractInsnNode instruction : instructions) {
            if (instruction.getType() == AbstractInsnNode.LABEL) {
                variableMap.updateScope(((LabelNode) instruction).getLabel());
            } else if (instruction.getOpcode() == Opcodes.ALOAD) {
                if (variableMap.isSearchable(((VarInsnNode) instruction).var))
                    instructions.insert(instruction, generateInstrumentation());
            }
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
