package com.github.sulir.runtimesearch.agent.analyzer;

import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Value;

import java.util.Objects;

public class StringValue implements Value {
    public enum Kind {
        MAYBE_STRING,
        MAYBE_STRING_ARRAY,
        LONG_OR_DOUBLE,
        OTHER
    }

    public static final StringValue MAYBE_STRING = new StringValue(Kind.MAYBE_STRING, 0);
    public static final StringValue LONG_OR_DOUBLE = new StringValue(Kind.LONG_OR_DOUBLE, 0);
    public static final StringValue OTHER = new StringValue(Kind.OTHER, 0);

    private final Kind kind;
    private final int arrayDimensions;

    public static StringValue newMaybeStringArray(int dimensions) {
        return new StringValue(Kind.MAYBE_STRING_ARRAY, dimensions);
    }

    public static StringValue fromBasicValue(BasicValue value) {
        if (value == null)
            return null;
        else if (value == BasicValue.LONG_VALUE || value == BasicValue.DOUBLE_VALUE)
            return StringValue.LONG_OR_DOUBLE;
        else if (value == BasicValue.REFERENCE_VALUE)
            throw new IllegalArgumentException("Cannot convert reference-typed value");
        else
            return StringValue.OTHER;
    }

    private StringValue(Kind kind, int arrayDimensions) {
        this.kind = kind;
        this.arrayDimensions = arrayDimensions;
    }

    public StringValue removeDimension() {
        switch (kind) {
            case MAYBE_STRING_ARRAY:
                if (arrayDimensions > 1)
                    return newMaybeStringArray(arrayDimensions - 1);
                else
                    return MAYBE_STRING;
            case OTHER:
                return OTHER;
            default:
                throw new IllegalStateException("Cannot remove array dimension");
        }
    }

    @Override
    public int getSize() {
        return (kind == Kind.LONG_OR_DOUBLE) ? 2 : 1;
    }

    public StringValue merge(StringValue second) {
        if (this == MAYBE_STRING || second == MAYBE_STRING)
            return MAYBE_STRING;

        if (this == LONG_OR_DOUBLE || second == LONG_OR_DOUBLE)
            return LONG_OR_DOUBLE;

        if (kind == Kind.MAYBE_STRING_ARRAY && second.kind == Kind.MAYBE_STRING_ARRAY)
            return newMaybeStringArray(Math.min(arrayDimensions, second.arrayDimensions));

        if (kind == Kind.MAYBE_STRING_ARRAY)
            return this;

        if (second.kind == Kind.MAYBE_STRING_ARRAY)
            return second;

        return OTHER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringValue that = (StringValue) o;
        return arrayDimensions == that.arrayDimensions && kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, arrayDimensions);
    }

    public String toString() {
        switch (kind) {
            case MAYBE_STRING:
                return "S";
            case MAYBE_STRING_ARRAY:
                return String.valueOf(arrayDimensions);
            case LONG_OR_DOUBLE:
                return "L";
            case OTHER:
                return "O";
            default:
                throw new AssertionError();
        }
    }
}
