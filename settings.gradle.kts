plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
}

refreshVersions {
    rejectVersionIf {
        @Suppress("UnstableApiUsage")
        (moduleId.group in listOf("org.junit.jupiter", "org.junit.platform") && candidate.value[0] > '5') ||
            candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
    }
}

rootProject.name = "runtimesearch"
include("shared", "agent", "plugin")
project(":shared").name = "runtimesearch-shared"
project(":agent").name = "runtimesearch-agent"
project(":plugin").name = "runtimesearch-plugin"
