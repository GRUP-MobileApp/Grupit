val realmVersion: String by project
val kotlinVersion: String by project
val koinAndroidVersion: String by project
val composeVersion: String by project

plugins {
    id("com.android.application")
    kotlin("android")
    id("io.realm.kotlin")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "com.grup.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.ui:ui-tooling:1.0.0-alpha07")
    implementation("androidx.navigation:navigation-compose:2.4.1")

    // Realm
    implementation("io.realm.kotlin:library-base:$realmVersion")

    // Koin
    implementation("io.insert-koin:koin-androidx-compose:$koinAndroidVersion")
}
