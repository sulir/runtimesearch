package com.github.sulir.runtimesearch.agent.transformer;

import com.github.sulir.runtimesearch.agent.Check;
import com.github.sulir.runtimesearch.agent.analyzer.StackAnalyzer;
import com.github.sulir.runtimesearch.agent.analyzer.StringValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;

public class MethodTransformer implements Opcodes {
    public static final String CHECK_CLASS = Type.getType(Check.class).getInternalName();

    private final String className;
    private final MethodNode method;
    private final InsnList instructions;

    public MethodTransformer(String className, MethodNode method) {
        this.className = className;
        this.method = method;
        this.instructions = method.instructions;
    }

    public void transform() throws AnalyzerException {
        StackAnalyzer analyzer = new StackAnalyzer(className, method);
        analyzer.analyze();

        int i = 0;
        for (AbstractInsnNode instruction : instructions) {
            Type returnType;
            switch (instruction.getOpcode()) {
                case LDC:
                case ALOAD:
                case AALOAD:
                case GETSTATIC:
                case GETFIELD:
                    if (analyzer.getStackTopAfter(i) == StringValue.MAYBE_STRING)
                        instrument(instruction);
                    break;
                case INVOKEVIRTUAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                    returnType = Type.getType(((MethodInsnNode) instruction).desc).getReturnType();
                    if (new ObjectType(returnType).canBeString())
                        instrument(instruction);
                    break;
                case INVOKEDYNAMIC:
                    returnType = Type.getType(((InvokeDynamicInsnNode) instruction).desc).getReturnType();
                    if (new ObjectType(returnType).canBeString())
                        instrument(instruction);
                    break;
                case INVOKESPECIAL:
                    MethodInsnNode invokespecial = (MethodInsnNode) instruction;

                    if (new ObjectType(Type.getType(invokespecial.desc).getReturnType()).canBeString())
                        instrument(invokespecial);
                    else if (invokespecial.owner.equals(ObjectType.STRING) && invokespecial.name.equals("<init>"))
                        instrumentConstructor(invokespecial);
                    break;
            }
            i++;
        }
    }

    private void instrument(AbstractInsnNode instruction) {
        AbstractInsnNode dup = new InsnNode(Opcodes.DUP);
        instructions.insert(instruction, dup);
        instructions.insert(dup, generateInvokestatic());
    }

    private void instrumentConstructor(MethodInsnNode invokespecial) {
        Type[] arguments = Type.getArgumentTypes(invokespecial.desc);
        InsnList copy = new InsnList();

        for (int i = arguments.length - 1; i >= 0; i--)
            copy.add(new VarInsnNode(arguments[i].getOpcode(Opcodes.ISTORE), method.maxLocals + i));
        copy.add(new InsnNode(Opcodes.DUP));
        for (int i = 0; i < arguments.length; i++)
            copy.add(new VarInsnNode(arguments[i].getOpcode(Opcodes.ILOAD), method.maxLocals + i));

        instructions.insertBefore(invokespecial, copy);
        instructions.insert(invokespecial, generateInvokestatic());
    }

    private AbstractInsnNode generateInvokestatic() {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, CHECK_CLASS, "perform", "(Ljava/lang/Object;)V");
    }
}
