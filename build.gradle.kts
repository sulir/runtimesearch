import fr.brouillard.oss.gradle.plugins.JGitverPluginExtensionBranchPolicy

plugins {
    java
    idea
    id("fr.brouillard.oss.gradle.jgitver")
}

jgitver {
    useDirty = true

    val policy = JGitverPluginExtensionBranchPolicy()
    policy.pattern("(.*)")
    policy.transformations(listOf("IGNORE"))
    policies.add(policy)
}

allprojects {
    group = "com.github.sulir.runtimesearch"

    project.afterEvaluate {
        val release = "(\\d+.\\d+).0".toRegex().matchEntire(version.toString())
        version = if (release != null) release.groups[1]!!.value else "snapshot"
    }
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
        testImplementation("org.junit.jupiter:junit-jupiter:_")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:_")
        testRuntimeOnly("junit:junit:_")
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