plugins {
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")

}

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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(project(mapOf("path" to ":data")))
    val room_version = "2.5.1"

    val composeBom = platform("androidx.compose:compose-bom:2023.01.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")

    // Material Design 3
    implementation("androidx.compose.material3:material3")

    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation("androidx.compose.material:material-icons-core")
    // Optional - Add full set of material icons
    implementation("androidx.compose.material:material-icons-extended")
    // Optional - Add window size utils
    implementation("androidx.compose.material3:material3-window-size-class")

    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.7.2")

    // for unlock app
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha05")

    // for DI
    implementation("com.google.dagger:hilt-android:2.48.1")

    // lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // for datastore
    implementation("androidx.datastore:datastore:1.0.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(mapOf("path" to ":core")))
    implementation(project(mapOf("path" to ":extension")))
    implementation(project(mapOf("path" to ":source-api")))
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")

    //ui lib
    implementation("com.github.tachiyomiorg:DirectionalViewPager:1.0.0") {
        exclude(group = "androidx.viewpager", module = "viewpager")
    }

    // image loading
    implementation("com.github.tachiyomiorg:subsampling-scale-image-view:c8e2650")/*{
        exclude(module = "image-decoder")
    }
    implementation("com.github.tachiyomiorg:image-decoder:16eda64574")*/

    // for room

/*    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")*/
    // To use Kotlin Symbol Processing (KSP)
    //noinspection GradleDependency
    ksp("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    // optional - Test helpers
    testImplementation("androidx.room:room-testing:$room_version")

    // optional - Paging 3 Integration
    implementation("androidx.room:room-paging:$room_version")

    // end room

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
// Allow references to generated code
kapt {
    correctErrorTypes = true
}