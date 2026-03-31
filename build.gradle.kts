@file:Suppress("SpellCheckingInspection")

plugins {
	id("net.fabricmc.fabric-loom-remap")
}

version = providers.gradleProperty("mod_version").get()
group = providers.gradleProperty("maven_group").get()

base {
	archivesName = providers.gradleProperty("archives_base_name")
}

repositories {
	maven {
			name = "ParchmentMC"
			url = uri("https://maven.parchmentmc.org")
		}
	maven {
		name = "Figura Maven"
		url = uri("https://maven.figuramc.org/snapshots")
		content {
			includeGroup("org.figuramc")
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	mappings(loom.officialMojangMappings())

	modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}")
	modImplementation("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}")

	modCompileOnly("org.figuramc:figura-fabric:${providers.gradleProperty("figura_version").get()}")
}

tasks.processResources {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 21
}

java {
	withSourcesJar()

	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

tasks.jar {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

tasks.named("runDatagen") {
	outputs.dir("src/main/generated")
}
