package com.github.sulir.runtimesearch.agent;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;

public class ClassTransformer {
    private final byte[] bytes;

    public ClassTransformer(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] transform() {
        ClassReader reader = new ClassReader(bytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM7, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,
                                             String signature, String[] exceptions) {
                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodNode(Opcodes.ASM7, access, name, desc, signature, exceptions) {
                    @Override
                    public void visitEnd() {
                        if (instructions.size() != 0 && (access & Opcodes.ACC_SYNTHETIC) == 0)
                            new MethodTransformer(this).transform();

                        accept(methodVisitor);
                    }
                };
            }
        };

        reader.accept(classVisitor, 0);
        return writer.toByteArray();
    }
}
