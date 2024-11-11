import fr.brouillard.oss.gradle.plugins.JGitverPluginExtensionBranchPolicy

plugins {
    java
    idea
    id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
}

jgitver {
    useDirty = true

    val policy = JGitverPluginExtensionBranchPolicy()
    policy.pattern("(.*)")
    policy.transformations(listOf("IGNORE"))
    policies.add(policy)
}

tasks.dependencyUpdates {
    revision = "release"
    rejectVersionIf {
        candidate.version.contains("-")
    }
}

allprojects {
    group = "com.github.sulir.runtimesearch"

    project.afterEvaluate {
        val release = "(\\d+.\\d+).0".toRegex().matchEntire(version.toString())
        version = if (release != null) release.groups[1]!!.value else "snapshot"
    }

    apply(plugin = "se.patrikerdes.use-latest-versions")
    apply(plugin = "com.github.ben-manes.versions")
}

subprojects {
    apply(plugin = "java")
    java.sourceCompatibility = JavaVersion.VERSION_1_8
    tasks.compileJava {
        options.compilerArgs.add("-Xlint:-options")
    }

    apply(plugin = "idea")
    idea {
        module {
            isDownloadSources = true
            isDownloadJavadoc = true
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.3")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(rootProject.file("LICENSE.txt")) {
            into("META-INF/")
        }
    }
}