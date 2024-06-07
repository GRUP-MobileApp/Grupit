val koinVersion: String by project
val kotlinVersion: String by project
val composeVersion: String by project
val lifecycleVersion: String by project
val firebaseBOMVersion: String by project
val kotlinExtensionVersion: String by project
val activityComposeVersion: String by project

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
            storeFile = file("signedKey")
            storePassword = keystorePassword
            keyAlias = "upload"
            keyPassword = keystorePassword
        }
    }
    compileSdk = 34
    defaultConfig {
        applicationId = "com.grup.android"
        minSdk = 21
        targetSdk = 34
        versionCode = 4
        versionName = "1.1"
        signingConfig = signingConfigs.getByName("release")
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = kotlinExtensionVersion
    }

    kotlinOptions {
        jvmTarget = "1.8"
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
    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.10.1")

    implementation("androidx.activity:activity-compose:$activityComposeVersion")

    // Koin
    implementation("io.insert-koin:koin-android:$koinVersion")

    // Google Play Services
    implementation ("com.google.android.gms:play-services-auth:21.1.1")
    implementation("com.google.android.gms:play-services-base:18.4.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:$firebaseBOMVersion"))

    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
