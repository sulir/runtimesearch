package com.github.sulir.runtimesearch.agent.analyzer;

import com.github.sulir.runtimesearch.agent.SimpleMethod;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackAnalyzerTest {
    private final FieldInsnNode getstatic = new FieldInsnNode(Opcodes.GETSTATIC, "Test", "test", "Ljava/lang/String;");
    private final MethodInsnNode invokestatic = new MethodInsnNode(Opcodes.INVOKESTATIC, "Test", "test",
            "()[Ljava/lang/String;");
    private final InsnNode iconst0 = new InsnNode(Opcodes.ICONST_0);
    private final InsnNode aaload = new InsnNode(Opcodes.AALOAD);
    private final InsnNode pop = new InsnNode(Opcodes.POP);
    private final InsnNode ret = new InsnNode(Opcodes.RETURN);

    @Test
    public void stackTopAfterStringGetstaticIsString() throws AnalyzerException {
        SimpleMethod method = new SimpleMethod(getstatic, pop, ret);

        StackAnalyzer analyzer = new StackAnalyzer(SimpleMethod.CLASS, method);
        analyzer.analyze();

        StringValue stackTopAfterGetstatic = analyzer.getStackTopAfter(0);
        assertEquals(StringValue.MAYBE_STRING, stackTopAfterGetstatic);
    }

    @Test
    public void stackTopAfterStringAaloadIsString() throws AnalyzerException {
        SimpleMethod method = new SimpleMethod(invokestatic, iconst0, aaload, pop, ret);

        StackAnalyzer analyzer = new StackAnalyzer(SimpleMethod.CLASS, method);
        analyzer.analyze();

        assertEquals(StringValue.newMaybeStringArray(1), analyzer.getStackTopAfter(0));
        assertEquals(StringValue.OTHER, analyzer.getStackTopAfter(1));
        assertEquals(StringValue.MAYBE_STRING, analyzer.getStackTopAfter(2));
    }
}
