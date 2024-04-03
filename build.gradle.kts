buildscript {
    val kotlinVersion: String by project
    val composeVersion: String by project
    val realmVersion: String by project
    val mokoResourcesVersion: String by project

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.compose:compose-gradle-plugin:$composeVersion")
        classpath("com.android.tools.build:gradle:8.3.1")
        classpath("io.realm.kotlin:gradle-plugin:$realmVersion")
        classpath("dev.icerock.moko:resources-generator:$mokoResourcesVersion")

        classpath("com.google.gms:google-services:4.4.1")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
