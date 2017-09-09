package com.github.sulir.runtimesearch.agent;

import javassist.*;

import java.io.ByteArrayInputStream;

public class ClassTransformer {
    private final ClassPool pool;

    public ClassTransformer(ClassLoader loader) {
        pool = new ClassPool(true);
        pool.appendClassPath(new LoaderClassPath(loader));
    }

    public byte[] transform(byte[] classBytes) throws Exception {
        CtClass clazz = pool.makeClass(new ByteArrayInputStream(classBytes));
        BytecodeTransformer bytecodeTransformer = new BytecodeTransformer(clazz);
        ExpressionTransformer expressionTransformer = new ExpressionTransformer(clazz);

        for (CtBehavior method : clazz.getDeclaredBehaviors()) {
            bytecodeTransformer.instrument(method);
            expressionTransformer.instrument(method);
        }

        return clazz.toBytecode();
    }
}
