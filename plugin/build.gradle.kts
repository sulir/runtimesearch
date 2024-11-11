import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("org.jetbrains.intellij.platform") version "2.1.0"
    id("org.jetbrains.changelog") version "2.2.1"
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(project(":runtimesearch-shared"))
    runtimeOnly(project(":runtimesearch-agent")) {
        isTransitive = false
    }

    intellijPlatform {
        intellijIdeaCommunity("2024.2.4")
        bundledPlugin("com.intellij.java")
        instrumentationTools()
    }
}

java.sourceCompatibility = JavaVersion.VERSION_17

intellijPlatform {
    pluginConfiguration {
        description = run {
            val lines = rootProject.file("README.md").readLines()
            val start = lines.indexOf("<!--plugin-desc-->")
            val end = lines.indexOf("<!--/plugin-desc-->")
            val description = lines.subList(start + 1, end).joinToString("\n")
            val readme = markdownToHTML(description)

            "<p>Searches the given text in all string expressions of a debugged Java SE program.<p>\n\n$readme"
        }

        changeNotes.set(provider {
            changelog.getAll().map {
                "<b>${it.key}</b>${changelog.renderItem(it.value.withHeader(false), Changelog.OutputType.HTML)}"
            }.joinToString("")
        })
    }

    pluginVerification {
        ides {
            select {
                types = listOf(IntelliJPlatformType.IntellijIdeaCommunity)
                channels = listOf(ProductRelease.Channel.RELEASE)
                sinceBuild = "223"
            }
        }
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
        channels = listOf(if (version.toString().contains("snapshot")) "snapshot" else "default")
    }
}

changelog {
    version = rootProject.version.toString()
    path = "${project.rootDir}/CHANGELOG.md"
    headerParserRegex = Regex("(\\d+\\.\\d+)")
    unreleasedTerm = "Unreleased"
}

tasks.buildPlugin {
    dependsOn(":runtimesearch-agent:jar")
    destinationDirectory = project.rootProject.file("dist")
}

tasks.runIde {
    dependsOn(":runtimesearch-agent:jar")
}

tasks.clean {
    delete(tasks.buildPlugin.get().archiveFile)
}
