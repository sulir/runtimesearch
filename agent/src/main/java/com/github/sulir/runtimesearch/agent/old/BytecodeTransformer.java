package com.github.sulir.runtimesearch.agent.old;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.*;
import javassist.bytecode.analysis.Analyzer;
import javassist.bytecode.analysis.Frame;
import javassist.bytecode.analysis.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BytecodeTransformer {
    private static final List<Integer> INSTRUMENTED = Arrays.asList(Opcode.LDC, Opcode.LDC_W,
            Opcode.ALOAD, Opcode.ALOAD_0, Opcode.ALOAD_1, Opcode.ALOAD_2, Opcode.ALOAD_3,
            Opcode.AALOAD, Opcode.GETFIELD, Opcode.GETSTATIC);

    private final Analyzer analyzer = new Analyzer();
    private final Type stringType;
    private final byte[] insertedBytes;

    public BytecodeTransformer(CtClass clazz) throws NotFoundException {
        stringType = Type.get(clazz.getClassPool().get("java.lang.String"));

        Bytecode inserted = new Bytecode(clazz.getClassFile().getConstPool());
        inserted.add(Opcode.DUP);
        inserted.addInvokestatic("com/github/sulir/runtimesearch/runtime/Check",
                "perform", "(Ljava/lang/Object;)V");
        insertedBytes = inserted.get();
    }

    public void instrument(CtBehavior method) throws BadBytecode {
        MethodInfo methodInfo = method.getMethodInfo();
        Frame[] frames = analyzer.analyze(method.getDeclaringClass(), methodInfo);
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        if (codeAttribute == null)
            return;

        CodeIterator code = codeAttribute.iterator();
        List<Boolean> isString = new ArrayList<>();

        iterateCode(code, () -> {
            Frame frame = frames[code.lookAhead()];
            Type type = frame.peek();

            if (type == Type.TOP)
                type = frame.getStack(frame.getTopIndex() - 1);

            isString.add(stringType.isAssignableFrom(type));
        });

        code.begin();
        Iterator<Boolean> isStringIterator = isString.iterator();

        iterateCode(code, () -> {
            if (isStringIterator.next())
                code.insertEx(insertedBytes);
        });

        codeAttribute.computeMaxStack();
    }

    private void iterateCode(CodeIterator code, Iteration iteration) throws BadBytecode {
        while (code.hasNext()) {
            int index = code.next();
            int opcode = code.byteAt(index);

            if (opcode == Opcode.WIDE)
                opcode = code.byteAt(index + 1);

            if (INSTRUMENTED.contains(opcode)) {
                iteration.run();
            }
        }
    }

    @FunctionalInterface
    private interface Iteration {
        void run() throws BadBytecode;
    }
}
