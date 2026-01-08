import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    packaging {
        // Exclude the conflicting files
        exclude(
            pattern = "META-INF/DEPENDENCIES"
        )
        exclude(
            pattern = "META-INF/LICENSE"
        )
        exclude(
            pattern = "META-INF/NOTICE"
        )
    }
    namespace = "com.example.myoffice"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myoffice"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.mariadb.java.client)
    implementation(libs.cardview)
    implementation(libs.recyclerview.v121)
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.google.api.client)
    implementation(libs.google.oauth.client)
    implementation(libs.google.auth.library.oauth2.http)
    implementation(libs.firebase.messaging)
    implementation(libs.material.v160)
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
}