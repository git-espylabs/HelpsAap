plugins {
    id (Plugins.ANDROID_APPLICATION)
    id (Plugins.JETBRAINS_KOTLIN_ANDROID)
    id(Plugins.KOTLIN_ANDROID)
    id(Plugins.KOTLIN_KAPT)
    id(Plugins.KOTLIN_NAVIGATION_SAFE_ARGS)
    id(Plugins.DAGGER_HILT)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}


android {

    compileSdk = ConfigData.compileSdkVersion

    signingConfigs {
        create("release") {
            keyAlias = KeyHelper.getValue(KeyHelper.KEY_ALIAS)
            keyPassword = KeyHelper.getValue(KeyHelper.KEY_PASS)
            storeFile = file(KeyHelper.getValue(KeyHelper.KEY_STORE_FILE))
            storePassword = KeyHelper.getValue(KeyHelper.KEY_STORE_PASS)
        }
    }

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
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions.add(ConfigData.dimension)
    productFlavors {
        create("staging") {
            dimension = ConfigData.dimension
            applicationIdSuffix = ".staging"
            versionNameSuffix = ".staging"
        }

        create("production") {
            dimension = ConfigData.dimension
            versionNameSuffix = ""
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

    applicationVariants.all {
        val appBaseUrl = when (flavorName) {
            "staging" -> "helpadmin.espylabs.com/public/api/"
            "production" -> "helpadmin.espylabs.com/public/api/"
            else -> null
        }
        val imageUrl = when (flavorName) {
            "staging" -> "helpadmin.espylabs.com/public/img/"
            "production" -> "helpadmin.espylabs.com/public/img/"
            else -> null
        }

        appBaseUrl?.let {
            buildConfigField("String", "BASE_URL", "\"https://$it\"")
        }

        imageUrl?.let {
            buildConfigField("String", "IMAGE_URL", "\"https://$it\"")
        }
    }


}

dependencies {

    Libs.implementations.forEach(::implementation)
    Libs.kaptDependencyNotions.forEach(::kapt)
    Libs.testImplementations.forEach(::testImplementation)
    Libs.androidTestImplementations.forEach(::androidTestImplementation)
}

kapt {
    correctErrorTypes = true
}