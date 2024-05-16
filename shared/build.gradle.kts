val ktorVersion: String by project
val koinVersion: String by project
val realmVersion: String by project
val kamelVersion: String by project
val napierVersion: String by project
val composeVersion: String by project
val voyagerVersion: String by project
val lifecycleVersion: String by project
val firebaseBOMVersion: String by project
val mokoResourcesVersion: String by project
val kotlinExtensionVersion: String by project
val activityComposeVersion: String by project

val keystorePassword: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.9.23"
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("io.realm.kotlin")
    id("dev.icerock.mobile.multiplatform-resources")
}

// CocoaPods requires the podspec to have a version.
version = "1.0"
kotlin {
    androidTarget()
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

        pod("FirebaseMessaging")
        pod("GoogleSignIn")

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.RELEASE
    }
    sourceSets {
        commonMain {
            dependencies {
                // Logger
                implementation("io.github.aakira:napier:$napierVersion")

                // Kotlin Libraries
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.ui)

                // Voyager
                implementation("cafe.adriel.voyager:voyager-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-tab-navigator:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-transitions:$voyagerVersion")
                implementation("cafe.adriel.voyager:voyager-koin:$voyagerVersion")

                // MOKO
                implementation("dev.icerock.moko:resources:$mokoResourcesVersion")
                implementation("dev.icerock.moko:resources-compose:$mokoResourcesVersion")
                implementation("dev.icerock.moko:media:0.11.0")
                implementation("dev.icerock.moko:media-compose:0.11.0")
                implementation("dev.icerock.moko:permissions-compose:0.16.0")

                // Kamel
                implementation("media.kamel:kamel-image:$kamelVersion")
                implementation("media.kamel:kamel-core:$kamelVersion")

                // Realm
                implementation("io.realm.kotlin:library-base:$realmVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

                // Realm Sync
                implementation("io.realm.kotlin:library-sync:$realmVersion")

                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")

                // Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                // Multiplatform Settings
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.1.1")
                implementation("com.russhwolf:multiplatform-settings-coroutines:1.1.1")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                // Koin Test
                implementation("io.insert-koin:koin-test:$koinVersion")
            }
        }
        androidMain {
            dependsOn(getByName("commonMain"))
            dependencies {
                implementation("androidx.activity:activity-compose:$activityComposeVersion")

                // Lifecycle
                implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
                implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

                // Ktor Client
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                // Import the Firebase BoM
                implementation(project.dependencies.platform("com.google.firebase:firebase-bom:$firebaseBOMVersion"))

                // Firebase Auth
                implementation("androidx.credentials:credentials:1.2.2")
                implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
                implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

                // Firebase Cloud Messaging
                implementation("com.google.firebase:firebase-messaging-ktx")
            }
        }
        androidNativeTest {
            dependsOn(getByName("commonMain"))
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        iosMain {
            dependsOn(getByName("commonMain"))
            getByName("iosX64Main").dependsOn(this)
            getByName("iosArm64Main").dependsOn(this)
            getByName("iosSimulatorArm64Main").dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        iosTest {
            dependsOn(getByName("commonTest"))
            getByName("iosX64Test").dependsOn(this)
            getByName("iosArm64Test").dependsOn(this)
            getByName("iosSimulatorArm64Test").dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
    }
}

android {
    signingConfigs {
        create("release") {
            storeFile = file("../androidApp/signedKey")
            storePassword = keystorePassword
            keyAlias = "upload"
            keyPassword = keystorePassword
        }
    }
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
        }
    }
    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = kotlinExtensionVersion
    }
    namespace = "com.grup"
}

multiplatformResources {
    multiplatformResourcesPackage = "com.grup.library"
}
