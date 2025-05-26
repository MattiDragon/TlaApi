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
    return extra.properties[name] as String? ?: throw IllegalStateException("Cannot find property $name")
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
        library("rei-config", "me.shedaniel.cloth:cloth-config-fabric:${getProp("cloth_config_version")}")
        library("emi", "dev.emi:emi-fabric:${getProp("emi_version")}")
        library("jei-api-common", "mezz.jei:jei-${getProp("minecraft_version")}-common-api:${getProp("jei_version")}")
        library("jei-api-fabric", "mezz.jei:jei-${getProp("minecraft_version")}-fabric-api:${getProp("jei_version")}")
        library("jei-fabric", "mezz.jei:jei-${getProp("minecraft_version")}-fabric:${getProp("jei_version")}")
    }
}