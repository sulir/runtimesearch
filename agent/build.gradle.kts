plugins {
    id("com.gradleup.shadow") version "8.3.8"
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-tree:9.7.1")
    implementation("org.ow2.asm:asm-analysis:9.7.1")
    implementation(project(":runtimesearch-shared"))
}

val distDir = project.rootProject.file("dist")
val agentArchive = project.name + ".jar"

tasks.shadowJar {
    destinationDirectory = distDir
    archiveFileName = agentArchive
    relocate("org.objectweb.asm", "com.github.sulir.runtimesearch.renamed.asm")

    manifest {
        attributes(mapOf(
                "Premain-Class" to "com.github.sulir.runtimesearch.agent.SearchAgent",
                "Boot-Class-Path" to agentArchive
        ))
    }
}

tasks.jar {
    destinationDirectory = distDir
    archiveFileName = agentArchive
    enabled = false
    dependsOn(tasks.shadowJar)
}

tasks.clean {
    delete(distDir.resolve(agentArchive))
}