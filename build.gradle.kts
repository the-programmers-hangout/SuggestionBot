import java.util.*

group = "me.ddivad"
version = "2.0.0"
description = "Bot to manage suggestions for a guild"

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.github.ben-manes.versions") version "0.36.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.23.3")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"

        Properties().apply {
            setProperty("name", project.name)
            setProperty("description", project.description)
            setProperty("version", version.toString())
            setProperty("url", "https://github.com/the-programmers-hangout/SuggestionBot")

            store(file("src/main/resources/bot.properties").outputStream(), null)
        }
    }

    shadowJar {
        archiveFileName.set("Suggestions.jar")
        manifest {
            attributes("Main-Class" to "me.ddivad.suggestions.MainKt")
        }
    }
}