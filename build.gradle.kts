plugins {
    id("org.springframework.boot") version "2.4.3" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version "1.4.30" apply false
    kotlin("plugin.spring") version "1.4.30" apply false
    kotlin("kapt") version "1.4.30" apply false
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.31" apply false
}

group = "me.pcuser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
