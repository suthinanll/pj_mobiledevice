plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.ass07"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.example.ass07"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("io.coil-kt:coil-compose:2.4.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.8.7")
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.volley)
    implementation(libs.play.services.base)
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.room.ktx)
<<<<<<< HEAD
<<<<<<< HEAD
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
=======
    implementation(libs.androidx.ui.test.android)
>>>>>>> f2bf95757895c6c1167a45e6248ffe12d75d0376
=======
    implementation(libs.androidx.benchmark.traceprocessor.android)
>>>>>>> eve
    testImplementation(libs.junit)
    implementation("io.coil-kt:coil-compose:2.0.0")
    androidTestImplementation(libs.junit.junit) // ใช้แสดงรูปภาพจาก URL หรือไฟล์


}