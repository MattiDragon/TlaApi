import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("maven-publish")
}

val minecraftVersion = property("minecraft_version") as String
val yarnMappings = property("yarn_mappings") as String
val loaderVersion = property("loader_version") as String
val fabricVersion = property("fabric_version") as String

version = property("mod_version") as String
group = "io.github.mattidragon"
base.archivesName = "TLA-Api"

repositories {
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/")
}

loom.splitEnvironmentSourceSets()

val SourceSetContainer.client: SourceSet
    get() = this["client"]
val SourceSetContainer.main: SourceSet
    get() = this["main"]

loom {
    mods.register("tla-api") {
        sourceSet(sourceSets.main)
        sourceSet(sourceSets.client)
    }

    runs["client"].ideConfigGenerated(false)
}

sourceSets.create("testmod") {
    compileClasspath += sourceSets.main.compileClasspath + sourceSets.main.output
    runtimeClasspath += sourceSets.main.runtimeClasspath + sourceSets.main.output
    compileClasspath += sourceSets.client.compileClasspath + sourceSets.client.output
    runtimeClasspath += sourceSets.client.runtimeClasspath + sourceSets.client.output
}

val SourceSetContainer.testmod: SourceSet
    get() = this["testmod"]

loom {
    createRemapConfigurations(sourceSets.testmod)
    mods.create("tlaapi_testmod").sourceSet(sourceSets.testmod)
}

arrayOf("rei", "emi").forEach { name ->
    val compilePaths = sourceSets.main.compileClasspath +
            sourceSets.main.output +
            sourceSets.client.compileClasspath +
            sourceSets.client.output
    val runtimePaths = sourceSets.main.runtimeClasspath +
            sourceSets.main.output +
            sourceSets.client.runtimeClasspath +
            sourceSets.client.output +
            sourceSets.testmod.output

    val sourceSet = sourceSets.create(name) {
        compileClasspath += compilePaths
        runtimeClasspath += runtimePaths

        resources.setSrcDirs(listOf("implSrc/$name/resources"))
        java.setSrcDirs(listOf("implSrc/$name/java"))
    }

    tasks[sourceSet.classesTaskName].dependsOn(sourceSets.testmod.classesTaskName)

    loom {
        runs.register("clientWith${name.uppercaseFirstChar()}") {
            inherit(runs["client"])
            ideConfigGenerated(true)
            configName = "Minecraft Client with ${name.uppercase()}"
            runDir("run/${name}")
            source(name)
        }

        createRemapConfigurations(sourceSet)
        mods["tla-api"].sourceSet(sourceSet)
    }

    tasks.jar {
        from(sourceSet.output)
        dependsOn(sourceSet.classesTaskName)
    }
}

dependencies {
    fun Provider<MinimalExternalModuleDependency>.withClassifier(classifier: String) = variantOf(this) { this.classifier(classifier) }

    minecraft(libs.minecraft)
    mappings(libs.fabric.yarn.withClassifier("v2"))
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    add("modReiCompileOnly", libs.rei.api)
    add("modReiCompileOnly", libs.rei.plugin.default)
    // For some reason arch isn't a transitive dependency of rei-api, so we need to manually add it to use a few classes
    add("modReiCompileOnly", libs.rei.architectury)
//    add("modReiCompileOnly", libs.rei.math)
    add("modReiCompileOnly", libs.rei.config)
    add("modReiRuntimeOnly", libs.rei.all)

    add("modEmiCompileOnly", libs.emi.withClassifier("api"))
    add("modEmiRuntimeOnly", libs.emi)
}

configurations.all {
    resolutionStrategy.force(libs.fabric.loader)
}

tasks.processResources  {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}"}
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
    }
}

//gradle.taskGraph.whenReady {
//    println("Found task graph: $this")
//    println("Found ${allTasks.size} tasks.")
//    allTasks.forEach { task ->
//        println()
//        println("----- $task -----")
//        println("depends on tasks: " + task.dependsOn.toList())
//        println("inputs: ")
//        task.inputs.files.files.map { f -> println(" - $f")}
//        println("outputs: ")
//        task.outputs.files.files.map { f -> println(" + $f")}
//    }
//}