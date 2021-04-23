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

## License

This plugin is available under the [Apache License 2.0](LICENSE.txt). It uses the [ASM library](https://asm.ow2.io) released under the [3-Clause BSD License](agent/src/main/resources/META-INF/LICENSE-ASM.txt).