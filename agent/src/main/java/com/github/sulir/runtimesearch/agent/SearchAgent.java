package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.transformer.ClassTransformer;
import com.github.sulir.runtimesearch.shared.Check;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class SearchAgent {
    private static final List<String> defaultExclude = Arrays.asList(
            "com.sun.", "java.", "javax.", "jdk.", "sun.",
            "com.intellij.rt.", "org.jetbrains.capture.", "org.groovy.debug.", "groovyResetJarjarAsm.",
            "org.objectweb.asm.", "com.github.sulir.runtimesearch."
    );

    static {
        Server.getInstance().start();
        Check.initialize();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        boolean filterSupplied = agentArgs != null && !agentArgs.isEmpty();
        Pattern include = Pattern.compile(filterSupplied ? agentArgs : ".*");
        List<String> exclude = filterSupplied ? Collections.emptyList() : defaultExclude;

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                try {
                    if (className == null)
                        return null;
                    className = className.replace('/', '.');

                    if (include.matcher(className).matches() && exclude.stream().noneMatch(className::startsWith)) {
                        ClassTransformer transformer = new ClassTransformer(className, classfileBuffer);
                        return transformer.transform();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return null;
            }
        });
    }
}
