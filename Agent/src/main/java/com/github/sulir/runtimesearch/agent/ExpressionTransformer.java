package com.github.sulir.runtimesearch.agent;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class ExpressionTransformer {
    private static final String CALL = "{Object result = $proceed($$);"
            + "com.github.sulir.runtimesearch.runtime.Check.perform(result); $_ = result;}";
    private final CtClass stringClass;

    public ExpressionTransformer(CtClass clazz) throws NotFoundException {
        stringClass = clazz.getClassPool().get("java.lang.String");
    }

    public void instrument(CtBehavior method) throws CannotCompileException {
        method.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall call) throws CannotCompileException {
                try {
                    if (stringClass.subtypeOf(call.getMethod().getReturnType())) {
                        call.replace(CALL);
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void edit(NewExpr call) throws CannotCompileException {
                try {
                    if (stringClass.subtypeOf(call.getConstructor().getDeclaringClass())) {
                        call.replace(CALL);
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
