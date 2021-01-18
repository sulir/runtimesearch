package com.github.sulir.runtimesearch.agent;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodTransformerTest {
    @Test
    public void aloadShouldBeInstrumented() throws AnalyzerException {
        InsnNode aconstNull = new InsnNode(Opcodes.ACONST_NULL);
        VarInsnNode astore1 = new VarInsnNode(Opcodes.ASTORE, 1);
        VarInsnNode aload1 = new VarInsnNode(Opcodes.ALOAD, 1);
        InsnNode pop = new InsnNode(Opcodes.POP);
        InsnNode ret = new InsnNode(Opcodes.RETURN);
        AbstractInsnNode[] instructions = new AbstractInsnNode[] {aconstNull, astore1, aload1, pop, ret};

        MethodNode method = new MethodNode(Opcodes.ACC_PUBLIC, "test", "()V", null, null);
        method.maxLocals = 2;
        method.maxStack = 3;

        method.instructions = new InsnList();
        for (AbstractInsnNode instruction : instructions)
            method.instructions.add(instruction);

        new MethodTransformer(method).transform();

        AbstractInsnNode[] expectedResult = new AbstractInsnNode[] {
                aconstNull,
                astore1,
                aload1,
                method.instructions.get(3), // compare later, AbstractInsnNode does not implement equals()
                method.instructions.get(4),
                pop,
                ret};
        assertArrayEquals(expectedResult, method.instructions.toArray());
        assertEquals(method.instructions.get(3).getOpcode(), Opcodes.DUP);
        assertEquals(method.instructions.get(4).getOpcode(), Opcodes.INVOKESTATIC);
    }
}
