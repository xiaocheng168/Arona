plugins {
    id("java")
    kotlin("jvm")
    `maven-publish`
}

group = "cc.mcyx"
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
}


kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}