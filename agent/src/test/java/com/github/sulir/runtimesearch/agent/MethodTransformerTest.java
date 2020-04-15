package com.github.sulir.runtimesearch.agent;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodTransformerTest {
    @Test
    public void stringAloadShouldBeInstrumented() {
        LabelNode start = new LabelNode(new Label());
        LabelNode end = new LabelNode(new Label());
        VarInsnNode aload1 = new VarInsnNode(Opcodes.ALOAD, 1);

        MethodNode method = new MethodNode(Opcodes.ASM7);
        LocalVariableNode variable = new LocalVariableNode("var", "Ljava/lang/String;", null, start, end, 1);
        method.localVariables = Collections.singletonList(variable);

        method.instructions = new InsnList();
        method.instructions.add(start);
        method.instructions.add(aload1);
        method.instructions.add(end);

        new MethodTransformer(method).transform();

        AbstractInsnNode[] expectedResult = new AbstractInsnNode[] {
                start,
                aload1,
                method.instructions.get(2),
                method.instructions.get(3),
                end};
        assertArrayEquals(method.instructions.toArray(), expectedResult);
        assertEquals(method.instructions.get(2).getOpcode(), Opcodes.DUP);
        assertEquals(method.instructions.get(3).getOpcode(), Opcodes.INVOKESTATIC);
    }
}
