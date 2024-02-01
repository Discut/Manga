plugins {
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")

}

val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a"/*, "x86", "x86_64"*/)


android {
    namespace = "com.discut.manga"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.discut.manga"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += SUPPORTED_ABIS
        }
        splits {
            abi {
                isEnable = true
                reset()
                include(*SUPPORTED_ABIS.toTypedArray())
                isUniversalApk = true
            }
        }
    }

    buildTypes {
        debug {
            versionNameSuffix = "-debug"
            applicationIdSuffix = ".debug"
            isPseudoLocalesEnabled = true
        }
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

}
dependencies {
    implementation(project(mapOf("path" to ":common-res")))
    val roomVersion = "2.5.1"
    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    //val composeBom = platform("dev.chrisbanes.compose:compose-bom:2024.01.00-alpha01")

    implementation(project(mapOf("path" to ":data")))
    implementation(project(mapOf("path" to ":source-local")))
    implementation(project(mapOf("path" to ":core")))
    implementation(project(mapOf("path" to ":extension")))
    implementation(project(mapOf("path" to ":source-api")))

    // Base
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation(libs.material)

    //palette
    implementation(libs.palette)

    // Material Design 3
    implementation("androidx.compose.material3:material3:1.2.0-alpha12")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.material:material-icons-core:1.5.4")// MD icon
    implementation("androidx.compose.material:material-icons-extended:1.5.4")// Add full set of material icons
    implementation("androidx.compose.material3:material3-window-size-class")// Add window size utils
    implementation("androidx.activity:activity-compose:1.7.2")// Integration with activities
    implementation(libs.reorderable)
    implementation(libs.constraintlayout)
    implementation(libs.swipe)
    implementation(libs.workmanager)
    implementation(libs.preference.compose)

    // for unlock app
    implementation(libs.biometric.ktx)

    // for DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")

    // lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // navigation
    implementation(libs.navigation.compose)

    // for datastore
    implementation(libs.datastore)


    //Ui Lib
    implementation(libs.directionalviewpager) {
        exclude(group = "androidx.viewpager", module = "viewpager")
    }

    // Image Loading
    implementation(libs.subsamplingscaleimageview) {
        exclude(module = "image-decoder")
    }
    implementation(libs.image.decoder)
    implementation(platform(libs.coil.bom))
    implementation(libs.bundles.coil)

    //animate icon
    implementation(libs.lottie)

    // Room
    // To use Kotlin annotation processing tool (kapt)
    /*kapt("androidx.room:room-compiler:$room_version")*/
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)// Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.paging)// Paging 3 Integration
    testImplementation(libs.room.test)// Test helpers

    // Paging
    implementation(libs.paging.runtime)
    implementation(libs.paging.compose)

    //// Network
    // okhttp
    implementation(libs.bundles.okhttp)
    implementation(libs.bundles.serialization)

    //// Disk
    // cache
    implementation(libs.disklrucache)
    // stream function
    implementation(libs.okio)
    implementation(libs.accompanist.permissions)

    //// Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}