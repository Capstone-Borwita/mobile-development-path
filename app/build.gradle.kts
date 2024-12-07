plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("kotlin-parcelize")
}

android {
    namespace = "com.untillness.borwita"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.untillness.borwita"
        minSdk = 30
        targetSdk = 35
        versionCode = 4
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL_V1", "\"https://borwita-ktp-api.ignorelist.com/\"")
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Pull to refresh
    implementation(libs.androidx.swiperefreshlayout)

    // Shimmer
    implementation(libs.shimmer)

    // Share preference
    implementation(libs.androidx.preference)

    // Retrofit
    implementation(libs.logging.interceptor)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.retrofit2.retrofit)

    // Glide
    implementation(libs.glide)

    // Maps
    implementation(libs.play.services.maps)

    // Location
    implementation(libs.play.services.location)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Ucrop
    implementation(libs.ucrop)

    // Camerax
    // CameraX core library using the camera2 implementation
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    implementation(libs.androidx.camera.video)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation(libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.androidx.camera.extensions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}