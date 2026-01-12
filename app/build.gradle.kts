import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val naverKey = properties.getProperty("NAVERMAP_CLIENT_ID") ?: ""
val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""

android {
    namespace = "com.example.cafemap"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cafemap"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // BuildConfig에 카카오 키 추가
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        
        // Manifest에서 사용하기 위한 Placeholder 설정
        addManifestPlaceholders(mapOf(
            "NAVERMAP_CLIENT_ID" to naverKey,
            "KAKAO_NATIVE_APP_KEY" to kakaoKey
        ))
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

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    val fragmentVersion = "1.8.9"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.fragment:fragment-ktx:${fragmentVersion}")
    implementation("com.naver.maps:map-sdk:3.23.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    
    // Kakao Login SDK
    implementation(libs.kakao.user)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}