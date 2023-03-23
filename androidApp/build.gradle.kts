val koinVersion: String by project
val realmVersion: String by project
val kotlinVersion: String by project
val composeVersion: String by project
val lifecycleVersion: String by project
val navigationVersion: String by project
val coilComposeVersion: String by project
val accompanistVersion: String by project

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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        resources.excludes.add("META-INF/*")
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    // Shared
    implementation(project(":shared"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Datastore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")

    // Animation
    implementation("androidx.compose.animation:animation:1.4.0")

    // Coil
    implementation("io.coil-kt:coil-compose:$coilComposeVersion")

    // Image Cropper
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("io.github.mr0xf00:easycrop:0.1.1")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation("androidx.ui:ui-tooling:1.0.0-alpha07")

    // Realm
    implementation("io.realm.kotlin:library-base:$realmVersion")

    // Accompanist
    implementation("com.google.accompanist:accompanist-pager:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-pager-indicators:$accompanistVersion")

    // Google Play Services
    implementation ("com.google.android.gms:play-services-auth:20.4.1")
    implementation("com.google.android.gms:play-services-base:18.2.0")
    implementation("com.google.android.play:app-update:2.0.1")
    implementation("com.google.android.play:app-update-ktx:2.0.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:31.2.2"))

    //Firebase Crashlytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging-ktx")
}
