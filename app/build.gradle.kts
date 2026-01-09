plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.authdemo"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.authdemo"
        minSdk = 24
        targetSdk = 36
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.google.firebase.functions.ktx)
//    implementation(libs.firebase.functions.ktx)
    implementation(libs.play.services.auth)

        // Import the BoM first
        implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

        // Add the Functions library
        // Note: You no longer need "-ktx" at the end (e.g., firebase-functions-ktx)
        // if you are using recent Firebase versions.
        implementation("com.google.firebase:firebase-functions")
//    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-firestore")

}