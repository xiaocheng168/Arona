plugins {
    id("java")
    kotlin("jvm")
}

group = "cc.mcyx"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    implementation(fileTree("lib"))
}
kotlin {
    jvmToolchain(17)
}