val koinVersion: String by project
val kotlinVersion: String by project
val composeVersion: String by project
val lifecycleVersion: String by project
val coilComposeVersion: String by project

val keystorePassword: String by project

plugins {
    id("com.android.application")
    kotlin("android")
    id("io.realm.kotlin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("/Users/justinxu/Documents/keystore/signedkey")
            storePassword = keystorePassword
            keyAlias = "upload"
            keyPassword = keystorePassword
        }
    }
    compileSdk = 33
    defaultConfig {
        applicationId = "com.grup.android"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        signingConfig = signingConfigs.getByName("release")
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
    namespace = "com.grup.android"
}

dependencies {
    // Shared
    implementation(project(":shared"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.10.0")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.4.3")
    implementation("androidx.activity:activity-compose:1.7.1")
    implementation("androidx.compose.ui:ui-tooling:1.4.3")

    // Koin
    implementation("io.insert-koin:koin-android:$koinVersion")

    // Google Play Services
    implementation ("com.google.android.gms:play-services-auth:20.5.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("com.google.android.play:app-update:2.0.1")
    implementation("com.google.android.play:app-update-ktx:2.0.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")
}
