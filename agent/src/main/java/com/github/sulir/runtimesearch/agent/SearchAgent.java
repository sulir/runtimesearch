package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.runtime.Check;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

public class SearchAgent {
    static {
        Check.initialize();
        Check.runServer();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        Pattern shouldInstrument = Pattern.compile((agentArgs == null) ? ".*" : agentArgs);

        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                try {
                    if (className != null && shouldInstrument.matcher(className).matches()) {
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
