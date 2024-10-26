plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.skyradar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.skyradar"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Retrofit Dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.databinding:databinding-runtime:8.7.0")
    implementation("com.squareup.picasso:picasso:2.8")
    // Coroutines Dependencies
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    // Room
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    //ViewModel & livedata
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    //glide
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    //openstreetmap
    implementation("org.osmdroid:osmdroid-android:6.1.11")
    implementation("org.osmdroid:osmdroid-wms:6.1.11")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.11")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.11")
    implementation("com.google.android.gms:play-services-location:21.0.1")
}