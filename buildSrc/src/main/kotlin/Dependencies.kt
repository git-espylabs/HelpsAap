object Libs {
    /**
     *  To define dependencies
     * */


    val implementations = listOf(
        "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}",
        "androidx.core:core-ktx:${Versions.ktx}",
        "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}",
        "androidx.appcompat:appcompat:${Versions.appCompat}",
        "com.google.android.material:material:${Versions.material}",
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}",
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}",
        "androidx.lifecycle:lifecycle-runtime:${Versions.ktxLifeCycle}",
        "androidx.lifecycle:lifecycle-common-java8:${Versions.ktxLifeCycle}",
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.ktxLifeCycle}",
        "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.ktxLifeCycle}",
        "androidx.room:room-runtime:${Versions.room}",
        "androidx.room:room-ktx:${Versions.room}",
        "androidx.navigation:navigation-ui-ktx:${Versions.navigation}",
        "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}",
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutine}",
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutine}",
        "com.squareup.okhttp3:logging-interceptor:${Versions.okHttpLogging}",
        "com.squareup.retrofit2:retrofit:${Versions.retrofit}",
        "com.squareup.retrofit2:converter-gson:${Versions.retrofit}",
        "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.coroutinesAdapter}",
        "com.google.android.gms:play-services-location:${Versions.gLocation}",
        "org.apache.commons:commons-collections4:${Versions.apacheCommCollections}",
        "androidx.preference:preference-ktx:${Versions.preference}",
        "androidx.activity:activity:${Versions.androidxActivity}",
        "androidx.fragment:fragment:${Versions.androidxFragment}",
        "io.coil-kt:coil:${Versions.coil}",
        "com.github.bumptech.glide:glide:${Versions.glide}",
        "androidx.multidex:multidex:${Versions.multiDexVers}",
        "com.google.dagger:hilt-android:${Versions.hilt}"
    )

    val testImplementations = listOf(
        "junit:junit:${Versions.jUnit}"
    )

    val androidTestImplementations = listOf(
        "androidx.test.espresso:espresso-core:${Versions.espresso}",
        "androidx.test.ext:junit:${Versions.androidXjUnit}"
    )

    val kaptDependencyNotions = listOf(
        "androidx.room:room-compiler:${Versions.room}",
        "com.google.dagger:hilt-android-compiler:${Versions.hilt}",
        "androidx.hilt:hilt-compiler:${Versions.androidxHiltCompiler}"
    )
}

object Plugins {
    const val KOTLIN_GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val GOOGLE_SERVICE = "com.google.gms:google-services:${Versions.googleServices}"
    const val GRADLE_VERSIONS = "com.github.ben-manes:gradle-versions-plugin:${Versions.manesGradleVersion}"
    const val NAVIGATION_SFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val KOTLIN_SERIALIZATION_PLUGIN = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"

    /** Subsystem */
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val ANDROID_EXTENSIONS = "android.extensions"
    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_KAPT = "kapt"
    const val KOTLIN_PARCELIZE = "kotlin-parcelize"
    const val KOTLIN_SERIALIZATION = "kotlinx-serialization"
}



