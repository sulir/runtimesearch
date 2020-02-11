# RuntimeSearch

RuntimeSearch is a debugger extension which searches the given text in the values of all string expressions in a running program. For a demonstration, see the [website](https://sulir.github.io/runtimesearch/).

## Building

To build both the Java agent and the IDE plugin, run the command:

    ./gradlew build

## Running

1. To open IntelliJ IDEA with the plugin, use the command: `./gradlew runIde`
2. In the Run/Debug Configuration of the program to be instrumented, add a VM option: `-javaagent:$RUNTIMESEARCH_PATH/dist/runtimesearch-agent-1.0-SNAPSHOT.jar=$INSTRUMENT_REGEX`, where `$RUNTIMESEARCH_PATH` is the root path of RuntimeSearch source code and `$INSTRUMENT_REGEX` is a regular expression matching all classes which should be instrumented. These class names use slashes instead of dots (e.g., `com/company/.*`)
3. Add a helper breakpoint: Run > View Breakpoints > "+" > Java Exception Breakpoints, type "UnknownError", press OK.
4. Debug the program, use Navigate / Find in Runtime, etc.
