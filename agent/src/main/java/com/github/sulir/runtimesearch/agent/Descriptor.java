package com.github.sulir.runtimesearch.agent;

import java.util.stream.Stream;

public class Descriptor {
    private static final Stream<String> possibleStrings = Stream.of("Ljava/lang/String;", "Ljava/lang/Object;",
            "Ljava/io/Serializable;", "Ljava/lang/Comparable;", "Ljava/lang/CharSequence;");

    public static boolean canBeString(String descriptor) {
        return possibleStrings.anyMatch(s -> s.equals(descriptor));
    }
}
