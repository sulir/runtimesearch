package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.tree.AbstractInsnNode;

public class ComparableInstruction {
    private final int opcode;

    public ComparableInstruction(int opcode) {
        this.opcode = opcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractInsnNode)
            return opcode == ((AbstractInsnNode) obj).getOpcode();
        else
            return false;
    }
}
