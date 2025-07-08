import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
    kotlin("plugin.serialization") version "2.0.21"
//    kotlin("plugin.parcelize") version "2.0.21"
}

android {
    namespace = "com.studiomk.matool"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.studiomk.matool"
        minSdk = 26
        targetSdk = 35
        versionCode = 8
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        defaultConfig {
            manifestPlaceholders["googleMapsKey"] =
                localProperties.getProperty("MAPS_API_KEY") ?: ""
        }
    }
    signingConfigs {
        create("release") {
            storeFile = file(project.property("KEYSTORE_FILE") as String)
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

    }
    kotlinOptions {
        jvmTarget = "11"
    }
    //JetPack
    buildFeatures {
        compose = true // Composeを有効にする
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "2.0.21"
    }
    
}

dependencies {
    implementation(libs.androidx.material3.android)
    implementation(libs.material)
    implementation(libs.protolite.well.known.types)

    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Choose one of the following:
    // Material Design 3
    implementation(libs.androidx.material3)
    // or Material Design 2
    implementation(libs.androidx.compose.material.material)
    // or skip Material Design and build directly on top of foundational components
    implementation(libs.androidx.foundation)
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
    implementation(libs.ui)

    // Android Studio Preview support
    implementation(libs.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    // UI Tests
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Optional - Included automatically by material, only add when you need
    // the icons but not the material library (e.g. when using Material3 or a
    // custom design system based on Foundation)
    implementation(libs.androidx.material.icons.core)
    // Optional - Add full set of material icons
    implementation(libs.androidx.material.icons.extended)
    // Optional - Add window size utils
    implementation(libs.androidx.adaptive)

    // Optional - Integration with activities
    implementation(libs.androidx.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Optional - Integration with LiveData
    implementation(libs.androidx.runtime.livedata)
    // Optional - Integration with RxJava
    implementation(libs.androidx.runtime.rxjava2)
    implementation (libs.androidx.activity.compose.v170)

    // Koin core for Kotlin
    implementation (libs.koin.core)
    implementation (libs.koin.android)
    testImplementation (libs.koin.test)
    implementation(libs.koin.androidx.compose)
    //cupertino ui
    implementation(libs.cupertino)
    //JSON
    implementation(libs.kotlinx.serialization.json)
    //GoogleMap
    implementation (libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.maps.compose.v660)
    implementation(libs.maps.compose.utils) //Option
    implementation(libs.maps.compose.widgets) //Option
    //Permission
    implementation(libs.accompanist.permissions)
    //AWS Amplify
    implementation(libs.core)
    implementation(libs.aws.auth.cognito)
    //ktca
    implementation("com.github.kmatsushita1012.ktca:ktca-core:b1ac45485f")
    implementation("com.github.kmatsushita1012.ktca:ktca-ui:b1ac45485f")
    ksp("com.github.kmatsushita1012.ktca:ktca-processor:b1ac45485f")
    //Picker
    implementation("com.seo4d696b75.compose:material3-picker:0.1.5")
    //launcher
    implementation ("androidx.core:core-splashscreen:1.0.0")

}