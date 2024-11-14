package com.github.sulir.runtimesearch.agent.transformer;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassTransformer {
    public static final int ASM_VERSION = Opcodes.ASM9;
    private static final Logger logger = Logger.getLogger(ClassTransformer.class.getName());

    private final String className;
    private final byte[] bytes;

    public ClassTransformer(String className, byte[] bytes) {
        this.className = className;
        this.bytes = bytes;
    }

    public byte[] transform() {
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor classVisitor = new ClassVisitor(ASM_VERSION, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,
                                             String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodNode(ASM_VERSION, access, name, desc, signature, exceptions) {
                    @Override
                    public void visitEnd() {
                        try {
                            if (instructions.size() != 0 && (access & Opcodes.ACC_SYNTHETIC) == 0)
                                new MethodTransformer(className, this).transform();
                        } catch (AnalyzerException e) {
                            logger.log(Level.WARNING, "Failed to transform method " + name + " in " + className, e);
                        }

                        accept(methodVisitor);
                    }
                };
            }
        };

        reader.accept(classVisitor, 0);
        return writer.toByteArray();
    }
}
