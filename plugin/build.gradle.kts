import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
import org.jetbrains.intellij.platform.gradle.models.ProductRelease

plugins {
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog")
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
        intellijIdea("2025.3.3")
        bundledPlugin("com.intellij.java")
        pluginVerifier()
    }
}

java.sourceCompatibility = JavaVersion.VERSION_21
val lowestBuild = "251"

intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = lowestBuild
            untilBuild = provider { null }
        }

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
                sinceBuild = lowestBuild
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

tasks.assemble {
    dependsOn(tasks.buildPlugin)
}

tasks.buildPlugin {
    dependsOn(":runtimesearch-agent:jar")
    destinationDirectory = project.rootProject.file("dist")
}

tasks.runIde {
    dependsOn(":runtimesearch-agent:jar")
    jvmArgs("-Didea.load.plugins.id=com.github.sulir.runtimesearch")
}

tasks.clean {
    delete(tasks.buildPlugin.get().archiveFile)
}
