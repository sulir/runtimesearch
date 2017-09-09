package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.runtime.Check;

import java.lang.instrument.Instrumentation;
import java.util.regex.Pattern;

public class SearchAgent {
    static {
        Check.initialize();
        Check.runServer();
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        Pattern shouldInstrument = Pattern.compile((agentArgs == null) ? ".*" : agentArgs);

        inst.addTransformer((loader, className, classBeingRedefined, protectionDomain,
                             classfileBuffer) -> {
            try {
                if (className != null && shouldInstrument.matcher(className).matches()) {
                    ClassTransformer transformer = new ClassTransformer(loader);
                    return transformer.transform(classfileBuffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        });
    }
}
