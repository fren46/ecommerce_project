import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.spring") version "1.4.30"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.4.31"
    kotlin("kapt") version "1.4.30"
}

group = "com.ecomm"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.5")
    implementation ("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation( project(":commons"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.modelmapper:modelmapper:2.3.0")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.kafka:spring-kafka")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    compileOnly("org.mapstruct:mapstruct:1.3.1.Final")
    kapt("org.mapstruct:mapstruct-processor:1.3.1.Final")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
