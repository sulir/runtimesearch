# RuntimeSearch

RuntimeSearch is a debugger extension which searches the given text in the values of all string expressions in a running program.

## Building

1. Create an empty project in IntelliJ IDEA.
2. Import the supplied modules (Agent and Plugin) through File > Project Structure > Modules > "+" > Import Module.
3. Build the agent: Maven Projects > searchagent > Lifecycle > package.
4. Build the plugin: Build > Prepare Plugin Module For Deployment.

## Running

1. Install the plugin (File > Settings > Plugins > Install plugin from disk).
2. In the Run/Debug Configuration of the program to be instrumented, add a VM option: -javaagent:...PATH_TO_AGENT_DIR.../searchagent-1.0-SNAPSHOT.jar=...INSTRUMENTED_CLASSES_REGEX..., where the class names use a slash notation, e.g. javax/swing.
3. Add a helper breakpoint: Run > View Breakpoints > "+" > Java Exception Breakpoints, type "UnknownError", press OK.
4. Debug the program, use Navigate / Find in Runtime, etc.