import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("kotlin-kapt") // 이 줄을 추가하세요.
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

val naverKey = properties.getProperty("NAVERMAP_CLIENT_ID") ?: ""
val kakaoKey = properties.getProperty("KAKAO_NATIVE_APP_KEY") ?: ""
val googleWebClientId = properties.getProperty("GOOGLE_WEB_CLIENT_ID") ?: ""

android {
    namespace = "com.example.cafemap"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.cafemap"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // BuildConfig에 키 추가
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoKey\"")
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")

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
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.firebase:firebase-auth")

    // Credential Manager
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Kakao Login SDK
    implementation(libs.kakao.user)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}