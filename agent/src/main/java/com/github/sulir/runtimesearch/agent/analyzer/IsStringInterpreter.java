package com.github.sulir.runtimesearch.agent.analyzer;

import com.github.sulir.runtimesearch.agent.transformer.ClassTransformer;
import com.github.sulir.runtimesearch.agent.transformer.ObjectType;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Interpreter;

import java.util.List;

/**
 * A bytecode interpreter similar to {@link BasicInterpreter}, but focused on the recognition of string/non-string
 * values.
 */
public class IsStringInterpreter extends Interpreter<StringValue> implements Opcodes {
    private final BasicInterpreter basicInterpreter = new BasicInterpreter();

    public IsStringInterpreter() {
        super(ClassTransformer.ASM_VERSION);
    }

    @Override
    public StringValue newValue(Type type) {
        if (type == null)
            return StringValue.OTHER;

        switch (type.getSort()) {
            case Type.VOID:
                return null;
            case Type.LONG:
            case Type.DOUBLE:
                return StringValue.LONG_OR_DOUBLE;
            case Type.OBJECT:
                if (new ObjectType(type).canBeString())
                    return StringValue.MAYBE_STRING;
                else
                    return StringValue.OTHER;
            case Type.ARRAY:
                if (newValue(type.getElementType()) == StringValue.MAYBE_STRING)
                    return StringValue.newMaybeStringArray(type.getDimensions());
                else
                    return StringValue.OTHER;
            default:
                return StringValue.OTHER;
        }
    }

    @Override
    public StringValue newOperation(AbstractInsnNode insn) {
        switch (insn.getOpcode()) {
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
                return StringValue.LONG_OR_DOUBLE;
            case LDC:
                Object constant = ((LdcInsnNode) insn).cst;
                if (constant instanceof Long || constant instanceof Double)
                    return StringValue.LONG_OR_DOUBLE;
                else if (constant instanceof String)
                    return StringValue.MAYBE_STRING;
                else if (constant instanceof ConstantDynamic)
                    return newValue(Type.getType(((ConstantDynamic) constant).getDescriptor()));
                else
                    return StringValue.OTHER;
            case GETSTATIC:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEW:
                String internalName = ((TypeInsnNode) insn).desc;
                if (internalName.equals(ObjectType.STRING))
                    return StringValue.MAYBE_STRING;
                else
                    return StringValue.OTHER;
            default:
                return StringValue.OTHER;
        }
    }

    @Override
    public StringValue copyOperation(AbstractInsnNode insn, StringValue value) {
        return value;
    }

    @Override
    public StringValue unaryOperation(AbstractInsnNode insn, StringValue value) throws AnalyzerException {
        switch (insn.getOpcode()) {
            case GETFIELD:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEWARRAY:
                return StringValue.OTHER;
            case ANEWARRAY:
                Type elementType = Type.getObjectType(((TypeInsnNode) insn).desc);
                return newValue(Type.getType("[" + elementType.getDescriptor()));
            case CHECKCAST:
                return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
            default:
                BasicValue basicValue = basicInterpreter.unaryOperation(insn, null);
                return StringValue.fromBasicValue(basicValue);
        }
    }

    @Override
    public StringValue binaryOperation(AbstractInsnNode insn, StringValue value1, StringValue value2)
            throws AnalyzerException {
        if (insn.getOpcode() == AALOAD) {
            return value1.removeDimension();
        } else {
            BasicValue basicValue = basicInterpreter.binaryOperation(insn, null, null);
            return StringValue.fromBasicValue(basicValue);
        }
    }

    @Override
    public StringValue ternaryOperation(AbstractInsnNode insn, StringValue value1, StringValue value2,
                                        StringValue value3) {
        return null;
    }

    @Override
    public StringValue naryOperation(AbstractInsnNode insn, List<? extends StringValue> values) {
        String descriptor;
        switch (insn.getOpcode()) {
            case MULTIANEWARRAY:
                descriptor = ((MultiANewArrayInsnNode) insn).desc;
                return newValue(Type.getType(descriptor));
            case INVOKEDYNAMIC:
                descriptor = ((InvokeDynamicInsnNode) insn).desc;
                return newValue(Type.getReturnType(descriptor));
            default:
                descriptor = ((MethodInsnNode) insn).desc;
                return newValue(Type.getReturnType(descriptor));
        }
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, StringValue value, StringValue expected) {

    }

    @Override
    public StringValue merge(StringValue value1, StringValue value2) {
        return value1.merge(value2);
    }
}
