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
        "com.google.dagger:hilt-android:${Versions.hilt}",
        "com.squareup.moshi:moshi:${Versions.moshi}",
        "com.squareup.retrofit2:converter-moshi:${Versions.moshi_converter}",
        "com.google.android.gms:play-services-maps:${Versions.play_services_maps}",
        "androidx.cardview:cardview:${Versions.cardView}",
        "de.hdodenhof:circleimageview:${Versions.hdodenhof}",
        "io.github.chaosleung:pinview:${Versions.otp_view}",
        "com.codesgood:justifiedtextview:${Versions.justified_textview}",
        "com.razorpay:checkout:1.6.26"
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
        "androidx.hilt:hilt-compiler:${Versions.androidxHiltCompiler}",
        "com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}",

    )
}

object Plugins {
    const val GOOGLE_SERVICE = "com.google.gms:google-services:${Versions.googleServices}"
    const val NAVIGATION_SFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val KOTLIN_SERIALIZATION_PLUGIN = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"

    /** Subsystem */
    const val ANDROID_APPLICATION = "com.android.application"
    const val ANDROID_LIBRARY = "com.android.library"
    const val ANDROID_EXTENSIONS = "android.extensions"
    const val KOTLIN_ANDROID = "kotlin-android"
    const val KOTLIN_KAPT = "kotlin-kapt"
    const val KOTLIN_PARCELIZE = "kotlin-parcelize"
    const val KOTLIN_SERIALIZATION = "kotlinx-serialization"
    const val KOTLIN_NAVIGATION_SAFE_ARGS = "androidx.navigation.safeargs.kotlin"
    const val JETBRAINS_KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
    const val JETBRAINS_KOTLIN_SERIALIZATION = "org.jetbrains.kotlin.plugin.serialization"
    const val DAGGER_HILT = "dagger.hilt.android.plugin"
    const val GOOGLE_MAPS = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin"

    const val CLASS_PATH_ANDROID_GRADLE_PLUGIN = "com.android.tools.build:gradle:${Versions.gradlePlugin}"
    const val CLASS_PATH_KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val CLASS_PATH_NAVIGATION_SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    const val CLASS_PATH_DAGGER_HILT = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"

}



