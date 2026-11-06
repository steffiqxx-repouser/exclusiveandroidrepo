plugins {
   // plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.kotlin.serialization)
    //}
}

android {
    namespace = "com.example.aiassistantwithandroid"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.aiassistantwithandroid"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"http://10.0.2.2:8080/\""
            )
        }

        release {
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://YOUR_BACKEND_DOMAIN/\""
            )

            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true

    }
    buildFeatures {
        compose = true
        buildConfig = true

    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    coreLibraryDesugaring(
        "com.android.tools:desugar_jdk_libs:2.1.5"
    )
    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
    )

    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    implementation(
        "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0"
    )

    implementation("com.squareup.okhttp3:okhttp:4.12.0")


    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}