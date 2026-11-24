plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.lumen"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lumen"
        minSdk = 26
        targetSdk = 34
        versionCode = 14
        versionName = "0.1.14"
    }

    signingConfigs {
        create("release") {
            storeType = "pkcs12"
            val ksPath = System.getenv("KEYSTORE_PATH")
                ?: System.getenv("LUMEN_KEYSTORE_PATH")
                ?: project.file("app/lumen-release.jks").absolutePath
            storeFile = file(ksPath)
            storePassword = System.getenv("KEYSTORE_PASSWORD")
                ?: System.getenv("LUMEN_KEYSTORE_PASSWORD")
            keyAlias = "lumen"
            keyPassword = System.getenv("KEY_PASSWORD")
                ?: System.getenv("LUMEN_KEYSTORE_PASSWORD")
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            lint {
                abortOnError = false
            }
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
