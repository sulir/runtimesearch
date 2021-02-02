package com.github.sulir.runtimesearch.agent;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MethodTransformerTest {
    private final LdcInsnNode ldc = new LdcInsnNode("string");
    private final InsnNode aconstNull = new InsnNode(Opcodes.ACONST_NULL);
    private final VarInsnNode astore = new VarInsnNode(Opcodes.ASTORE, 1);
    private final VarInsnNode aload = new VarInsnNode(Opcodes.ALOAD, 1);
    private final InsnNode pop = new InsnNode(Opcodes.POP);
    private final InsnNode ret = new InsnNode(Opcodes.RETURN);
    private final ComparableInstruction dup = new ComparableInstruction(Opcodes.DUP);
    private final ComparableInstruction invokestatic = new ComparableInstruction(Opcodes.INVOKESTATIC);

    @Test
    public void stringLdcAndAloadAreInstrumented() throws AnalyzerException {
        SimpleMethod method = new SimpleMethod(ldc, astore, aload, pop, ret);
        new MethodTransformer(method.getClassName(), method).transform();

        Object[] expectedResult = new Object[] {ldc, dup, invokestatic, astore, aload, dup, invokestatic, pop, ret};
        assertArrayEquals(expectedResult, method.getInstructions());
    }

    @Test
    public void nullAloadIsNotInstrumented() throws AnalyzerException {
        SimpleMethod method = new SimpleMethod(aconstNull, astore, aload, pop, ret);
        AbstractInsnNode[] expectedUnchanged = method.getInstructions();
        new MethodTransformer(method.getClassName(), method).transform();

        assertArrayEquals(expectedUnchanged, method.getInstructions());
    }
}
