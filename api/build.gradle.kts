val ktorVersion: String by project
val logbackVersion: String by project
val kmongoVersion: String by project
val realmVersion: String by project
val koinVersion: String by project
val koinKtor: String by project
val testcontainersVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("io.ktor.plugin") version "2.1.2"
    id("io.realm.kotlin")
}

group = "com.grup"
version = "0.0.1"
application {
    mainClass.set("com.grup.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")

    // Ktor
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    // MongoDB and Realm
    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-id-serialization:$kmongoVersion")
    implementation("io.realm.kotlin:library-base:$realmVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    // Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-ktor:$koinKtor")
    implementation("io.insert-koin:koin-logger-slf4j:$koinKtor")
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")
}