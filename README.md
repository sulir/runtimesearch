# RuntimeSearch

[![Build status](https://github.com/sulir/runtimesearch/actions/workflows/build.yml/badge.svg)](https://github.com/sulir/runtimesearch/actions/workflows/build.yml)
[![Version](https://img.shields.io/jetbrains/plugin/v/com.github.sulir.runtimesearch)](https://plugins.jetbrains.com/plugin/16527-runtimesearch)

RuntimeSearch is a debugger extension which searches the given text in the values of all string expressions in a running Java SE program.

## Download

You can install the plugin from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/16527-runtimesearch) or directly from IntelliJ IDEA.

## Usage

<!--plugin-desc-->
![Screenshot](https://github.com/sulir/runtimesearch/blob/master/.github/images/screenshot.png?raw=true)

Before using, open the Debug Configuration you would like to use via **Run | Edit Configurations**. Under **RuntimeSearch Settings**, check *Enable RuntimeSearch for this configuration*. This may incur minor debugging performance overhead. You can also restrict the search scope to selected packages. Note that non-JVM (e.g., Android) targets are not supported.

To start searching, select **Run | Find in Runtime**, enter the searched string and press Find. If the program is not yet being debugged, a new process will be started. Then interact with the running application. As soon as any string expression in the program will contain the searched string (substrings are matched), the process will be paused. You can use traditional debugging actions (e.g., Step Over) or search for the next occurrence via **Run | Find Next in Runtime**.

For a tutorial and sample use cases, watch the [video](https://sulir.github.io/runtimesearch/#video) or read the [paper](https://sulir.github.io/runtimesearch/#article).
<!--/plugin-desc-->

## Building from Source

To build the project, run the command:

    ./gradlew build

The resulting plugin will be stored in `dist/runtimesearch-plugin-*.zip`. It can be installed using the *Install Plugin from Disk* command in IntelliJ IDEA. Alternatively, you can run it in a sandbox via `./gradlew runIde`.

## Internals

The project consists of an IntelliJ IDEA plugin and a bytecode [instrumentation agent](https://docs.oracle.com/en/java/javase/11/docs/api/java.instrument/java/lang/instrument/package-summary.html). The initial configuration is passed from the IDE to the agent via command-line arguments when executing the target program:

    java -javaagent:$RUNTIMESEARCH_PATH/dist/runtimesearch-agent.jar=$PORT \
        -Druntimesearch.include=$INCLUDE_PATTERN \
        -Druntimesearch.text=$TEXT \
        -Druntimesearch.case=$MATCH_CASE \
        -jar target.jar

`$RUNTIMESEARCH_PATH` is the path to the RuntimeSearch project, `$PORT` is the server port where the agent should listen for commands, `$INCLUDE_PATTERN` contains a regular expression deciding which classes should be instrumented (e.g., `com\.example\..*`), `$TEXT` is the text to search, `$MATCH_CASE` represents a searching option (true/false). The agent's argument and all system properties are optional.

The agent inserts a call to a checking method after each instruction which can push a String to the stack. Further communication (e.g., a change of the searched text) is performed via sockets &ndash; see the source code for details.

When an occurrence is found, a [BreakpointError](shared/src/main/java/com/github/sulir/runtimesearch/shared/BreakpointError.java) is thrown, and the program is suspended thanks to an exception breakpoint.

## License

This software is available under the [Apache License 2.0](LICENSE.txt). It uses the [ASM library](https://asm.ow2.io) released under the [3-Clause BSD License](agent/src/main/resources/META-INF/LICENSE-ASM.txt).