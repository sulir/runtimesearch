package com.github.sulir.runtimesearch.agent.transformer;

import org.objectweb.asm.Type;

public class ObjectType {
    public static final String STRING = "java/lang/String";

    private final Type type;

    public ObjectType(Type type) {
        this.type = type;
    }

    public boolean canBeString() {
        if (type.getSort() == Type.OBJECT) {
            switch (type.getInternalName()) {
                case STRING:
                case "java/lang/Object":
                case "java/io/Serializable":
                case "java/lang/Comparable":
                case "java/lang/CharSequence":
                    return true;
            }
        }

        return false;
    }
}
