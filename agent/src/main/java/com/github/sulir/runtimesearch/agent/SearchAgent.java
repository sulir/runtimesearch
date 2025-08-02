package com.github.sulir.runtimesearch.agent;

import com.github.sulir.runtimesearch.agent.transformer.ClassTransformer;
import com.github.sulir.runtimesearch.shared.SharedConfig;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class SearchAgent {
    private static final Logger logger = Logger.getLogger(SearchAgent.class.getName());
    private static final Pattern NUMBER = Pattern.compile("\\d+");
    private static final List<String> defaultExclude = Arrays.asList(
            "com.sun.", "java.", "javax.", "jdk.", "sun.",
            "com.intellij.rt.", "org.jetbrains.capture.", "org.groovy.debug.", "groovyResetJarjarAsm.",
            "com.github.sulir.runtimesearch."
    );

    @SuppressWarnings("unused")
    public static void premain(String agentArgs, Instrumentation inst) {
        Check.initialize();
        if (agentArgs != null && NUMBER.matcher(agentArgs).matches())
            Server.getInstance().start(Integer.parseInt(agentArgs));

        SearchAgent agent = new SearchAgent();
        agent.fixJbossClassLoading();

        String includeProperty = System.getProperty(SharedConfig.INCLUDE_PROPERTY, "");
        Pattern include = Pattern.compile(includeProperty.isEmpty() ? ".*" : includeProperty);
        List<String> exclude = includeProperty.isEmpty() ? defaultExclude : Collections.emptyList();
        agent.setupInstrumentation(inst, include, exclude);
    }

    private void fixJbossClassLoading() {
        String SYSTEM_PACKAGES = "jboss.modules.system.pkgs";
        String currentPackages = System.getProperty(SYSTEM_PACKAGES);
        boolean isSet = currentPackages != null && !currentPackages.isEmpty();
        String agentPackage = Check.class.getPackage().getName();
        System.setProperty(SYSTEM_PACKAGES, isSet ? agentPackage : currentPackages + "," + agentPackage);
    }

    private void setupInstrumentation(Instrumentation instrumentation, Pattern include, List<String> exclude) {
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                try {
                    if (className == null)
                        return null;
                    String name = className.replace('/', '.');

                    if (include.matcher(name).matches() && exclude.stream().noneMatch(name::startsWith))
                        return new ClassTransformer(className, classfileBuffer).transform();
                } catch (Throwable e) {
                    logger.log(Level.WARNING, "Failed to transform class " + className, e);
                }

                return null;
            }
        });
    }
}
