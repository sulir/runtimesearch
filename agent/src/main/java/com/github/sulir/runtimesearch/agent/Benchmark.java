package com.github.sulir.runtimesearch.agent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Benchmark {
    private static long startTime;
    private static long totalTime;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (startTime != 0)
                    Files.write(Paths.get("time.txt"), String.valueOf(totalTime / 1_000_000).getBytes());
            } catch (IOException e) { /* */ }
        }));
    }

    public static void start() {
        startTime = System.nanoTime();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> T end(T arg) {
        totalTime += System.nanoTime() - startTime;
        return arg;
    }
}
