plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.streamclienwithad"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.streamclienwithad"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    implementation("com.yandex.android:mobileads:6.1.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    val media3Version = "1.1.1"
    implementation ("androidx.media3:media3-exoplayer:$media3Version")
    implementation ("androidx.media3:media3-exoplayer-dash:$media3Version")
    implementation ("androidx.media3:media3-ui:$media3Version")
    implementation ("androidx.media3:media3-datasource-okhttp:$media3Version")
    implementation ("androidx.media3:media3-exoplayer-ima:$media3Version")
    implementation ("androidx.media3:media3-exoplayer-hls:$media3Version")
    implementation ("androidx.media3:media3-exoplayer-smoothstreaming:$media3Version")
    implementation ("androidx.media3:media3-session:$media3Version")


}