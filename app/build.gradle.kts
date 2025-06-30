
plugins {
    alias(libs.plugins.android.application)

    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.applepie"  // Thay bằng package của bạn
    compileSdk = 35  // hoặc phiên bản bạn muốn

    defaultConfig {
        applicationId = "com.example.applepie"  // Thay bằng package của bạn
        minSdk = 24
        targetSdk = 35
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
    packaging {
        resources {
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.glide)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)

    annotationProcessor(libs.glideCompiler)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.sun.mail:android-mail:1.6.6")
    implementation ("com.sun.mail:android-activation:1.6.6")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
}
