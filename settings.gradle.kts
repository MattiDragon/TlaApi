pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

fun getProp(name: String): String {
    return extra.properties[name] as String
}

dependencyResolutionManagement {
    versionCatalogs.create("libs") {
        library("fabric-api", "net.fabricmc.fabric-api:fabric-api:${getProp("fabric_version")}")
        library("fabric-loader", "net.fabricmc:fabric-loader:${getProp("loader_version")}")
        library("fabric-yarn", "net.fabricmc:yarn:${getProp("yarn_mappings")}")
        library("minecraft", "com.mojang:minecraft:${getProp("minecraft_version")}")

        library("rei-api", "me.shedaniel:RoughlyEnoughItems-api-fabric:${getProp("rei_version")}")
        library("rei-plugin-default", "me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:${getProp("rei_version")}")
        library("rei-all", "me.shedaniel:RoughlyEnoughItems-fabric:${getProp("rei_version")}")
        library("rei-architectury", "dev.architectury:architectury-fabric:${getProp("architectury_version")}")
        library("rei-math", "me.shedaniel.cloth:basic-math:${getProp("cloth_basic_math_version")}")
        library("rei-config", "me.shedaniel.cloth:cloth-config-fabric:${getProp("cloth_config_version")}")
        library("emi", "dev.emi:emi-fabric:${getProp("emi_version")}")
    }
}