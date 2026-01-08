plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
}

buildscript {
    dependencies {
        // ...
        classpath(libs.google.services)
        classpath(libs.gradle)
    }
}
