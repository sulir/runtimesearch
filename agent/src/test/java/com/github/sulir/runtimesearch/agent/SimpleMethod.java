package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.transformer.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class SimpleMethod extends MethodNode {
    public static final String CLASS = "pkg/Test";

    public SimpleMethod(AbstractInsnNode... instructions) {
        super(ClassTransformer.ASM_VERSION, Opcodes.ACC_PUBLIC, "test", "()V", null, null);
        maxLocals = 10;
        maxStack = 10;

        for (AbstractInsnNode instruction : instructions)
            this.instructions.add(instruction);
    }

    public AbstractInsnNode[] getInstructions() {
        return instructions.toArray();
    }
}
