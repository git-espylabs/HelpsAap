plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}


android {
    compileSdk = ConfigData.compileSdkVersion

    defaultConfig {
        applicationId = ConfigData.appId
        minSdk = ConfigData.minSdkVersion
        targetSdk = ConfigData.targetSdkVersion
        versionCode = ConfigData.versionCode
        versionName = ConfigData.versionName

        testInstrumentationRunner = ConfigData.testInstrumentationRunner
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions.add(ConfigData.dimension)
    productFlavors {
        create("staging") {
            applicationIdSuffix = ".staging"
            dimension = ConfigData.dimension
        }

        create("production") {
            dimension = ConfigData.dimension
        }
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    dataBinding {
        android.buildFeatures.dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    Libs.implementations.forEach(::implementation)
    kapt(Libs.ANDROIDX_ROOM_COMPILER)
    Libs.testImplementations.forEach(::testImplementation)
    Libs.androidTestImplementations.forEach(::androidTestImplementation)
}