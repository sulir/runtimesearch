# RuntimeSearch

RuntimeSearch is a debugger extension which searches the given text in the values of all string expressions in a running program. For a demonstration, see the [website](https://sulir.github.io/runtimesearch/).

## Building

To build both the Java agent and the IDE plugin, run the command:

    ./gradlew build

## Running

1. To open IntelliJ IDEA with the plugin, type the command: `./gradlew runIde`
2. Open the Run/Debug Configuration you would like to use. In the "RuntimeSearch Settings" tab/section, check "Enable RuntimeSearch for this configuration". You can also restrict the search scope to selected packages or classes.
3. Add a helper breakpoint: Run > View Breakpoints > "+" > Java Exception Breakpoints, type "UnknownError", press OK.
4. Debug the program, use Navigate / Find in Runtime, etc.
