package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SimpleMethod extends MethodNode {
    public SimpleMethod(AbstractInsnNode... instructions) {
        super(ClassTransformer.ASM_VERSION, Opcodes.ACC_PUBLIC, "test", "()V", null, null);
        maxLocals = 10;
        maxStack = 10;

        for (AbstractInsnNode instruction : instructions)
            this.instructions.add(instruction);
    }

    public String getClassName() {
        return "test/Test";
    }

    public AbstractInsnNode[] getInstructions() {
        return instructions.toArray();
    }
}
