buildscript {
    val kotlinVersion: String by project

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.android.tools.build:gradle:7.3.0")
        classpath("io.realm.kotlin:gradle-plugin:1.4.0-SNAPSHOT")
    }
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
