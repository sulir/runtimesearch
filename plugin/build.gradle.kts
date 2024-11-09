import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    id("org.jetbrains.changelog") version "2.2.1"
}

dependencies {
    implementation(project(":runtimesearch-shared"))
    runtimeOnly(project(":runtimesearch-agent")) {
        isTransitive = false
    }
}

java.sourceCompatibility = JavaVersion.VERSION_11
val intellijVersions = listOf("2020.3.4", "2021.1.3", "2021.2.4", "2021.3.3", "2022.1.4", "2022.2.4", "2022.3.1")

intellij {
    version = intellijVersions.last()
    plugins = listOf("java")
    updateSinceUntilBuild = false
}

changelog {
    version.set(rootProject.version.toString())
    path = "${project.rootDir}/CHANGELOG.md"
    headerParserRegex = Regex("(\\d+\\.\\d+)")
    unreleasedTerm = "Unreleased"
}

tasks.patchPluginXml {
    val firstVersion = intellijVersions[0].split(".")
    sinceBuild = firstVersion[0].substring(2) + firstVersion[1]

    pluginDescription = run {
        val lines = rootProject.file("README.md").readLines()
        val start = lines.indexOf("<!--plugin-desc-->")
        val end = lines.indexOf("<!--/plugin-desc-->")
        val description = lines.subList(start + 1, end).joinToString("\n")
        val readme = markdownToHTML(description, "\n")

        "<p>Searches the given text in all string expressions of a debugged Java SE program.<p>\n\n$readme"
    }

    changeNotes.set(provider {
        changelog.getAll().map {
            "<b>${it.key}</b>${changelog.renderItem(it.value.withHeader(false), Changelog.OutputType.HTML)}"
        }.joinToString("")
    })
}

tasks.buildSearchableOptions {
    enabled = false
}

tasks.buildPlugin {
    dependsOn(":runtimesearch-agent:jar")
    destinationDirectory = project.rootProject.file("dist")
}

tasks.runPluginVerifier {
    ideVersions = intellijVersions
}

tasks.publishPlugin {
    token = System.getenv("PUBLISH_TOKEN")
    channels = listOf(if (version.toString().contains("snapshot")) "snapshot" else "default")
}

tasks.runIde {
    dependsOn(":runtimesearch-agent:jar")
    jvmArgs("--add-exports", "java.base/jdk.internal.vm=ALL-UNNAMED")
}

tasks.clean {
    delete(tasks.buildPlugin.get().archiveFile)
}
