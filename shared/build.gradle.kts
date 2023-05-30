val awsVersion: String by project
val ktorVersion: String by project
val koinVersion: String by project
val imageLoaderVersion: String by project
val realmVersion: String by project
val composeVersion: String by project
val voyagerVersion: String by project
val lifecycleVersion: String by project
val coilComposeVersion: String by project
val napierVersion = "2.4.0"

val keystorePassword: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.20"
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("io.realm.kotlin")
    id("dev.icerock.mobile.multiplatform-resources")
}

// CocoaPods requires the podspec to have a version.
version = "1.0"
kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            // Mandatory properties
            // Configure fields required by CocoaPods.
            summary = "Some description for a Kotlin/Native module"
            homepage = "Link to a Kotlin/Native module homepage"
            // Framework name configuration. Use this property instead of deprecated 'frameworkName'
            baseName = "shared"
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin Libraries
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // UI

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)

                // Logger
                implementation("io.github.aakira:napier:$napierVersion")

                // Voyager
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")

                // MOKO
                implementation("dev.icerock.moko:resources:0.22.0")
                implementation("dev.icerock.moko:resources-compose:0.22.0")
                implementation("dev.icerock.moko:media:0.11.0")
                implementation("dev.icerock.moko:media-compose:0.11.0")
                implementation("dev.icerock.moko:permissions-compose:0.16.0")

                // Backend

                // Realm
                implementation("io.realm.kotlin:library-base:$realmVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

                // Realm Sync
                implementation("io.realm.kotlin:library-sync:$realmVersion")

                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")

                // Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // Koin Test
                implementation("io.insert-koin:koin-test:$koinVersion")
            }
        }
        val androidMain by getting {
            dependencies {
                // UI

                // Jetpack Compose
                implementation("androidx.compose.ui:ui:$composeVersion")
                implementation("androidx.activity:activity-compose:1.7.1")

                // Lifecycle
                implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")


                // Coil
                implementation("io.coil-kt:coil-compose:$coilComposeVersion")

                // Backend

                // Datastore
                implementation("androidx.datastore:datastore-preferences:1.0.0")

                // Ktor Client
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                // Google Play Services
                implementation ("com.google.android.gms:play-services-auth:20.5.0")

                // AWS
                implementation("aws.sdk.kotlin:s3:$awsVersion")

                // Import the Firebase BoM
                implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

                // Firebase Crashlytics
                implementation("com.google.firebase:firebase-analytics-ktx")
                implementation("com.google.firebase:firebase-crashlytics-ktx")

                // Firebase Cloud Messaging
                implementation("com.google.firebase:firebase-messaging-ktx")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
    }
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
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    namespace = "com.grup"
}

multiplatformResources {
    multiplatformResourcesPackage = "com.grup.library"
}
