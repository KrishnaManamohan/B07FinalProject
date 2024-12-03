plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.planetzeapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.planetzeapp"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}


dependencies {
    implementation ("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation ("com.google.firebase:firebase-firestore:24.5.0")
    implementation ("com.google.firebase:firebase-auth:21.0.8")
    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    // AndroidX and Material Design dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito:mockito-android:5.5.0")
    testImplementation("com.google.android.gms:play-services-tasks:18.2.0")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    testImplementation ("junit:junit:4.13.2")

    // Add MPAndroidChart dependency for charts (line chart, pie chart, etc.)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Use latest stable version
}