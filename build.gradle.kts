// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath(Plugins.CLASS_PATH_NAVIGATION_SAFE_ARGS)
        classpath(Plugins.CLASS_PATH_DAGGER_HILT)
    }
}

plugins {
    id (Plugins.ANDROID_APPLICATION) version Versions.gradlePlugin apply false
    id (Plugins.ANDROID_LIBRARY) version Versions.gradlePlugin apply false
    id (Plugins.JETBRAINS_KOTLIN_ANDROID) version Versions.kotlin apply false
    id(Plugins.JETBRAINS_KOTLIN_SERIALIZATION) version Versions.kotlin apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}