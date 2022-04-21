package com.janustech.helpsaap.network

import android.content.Context
import com.google.gson.GsonBuilder
import com.janustech.helpsaap.BuildConfig
import com.janustech.helpsaap.app.App
import com.janustech.helpsaap.network.requests.GeneralApis
import com.janustech.helpsaap.repositories.AppIntroRepository
import com.janustech.helpsaap.repositories.AppIntroRepositoryImpl
import com.janustech.helpsaap.repositories.ProfileRepository
import com.janustech.helpsaap.repositories.ProfileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@Suppress("unused")
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): App {
        return app as App
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
    }

    private val READ_TIMEOUT = 30
    private val WRITE_TIMEOUT = 30
    private val CONNECTION_TIMEOUT = 10
    private val CACHE_SIZE_BYTES = 10 * 1024 * 1024L // 10 MB

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        cache: Cache
    ): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.cache(cache)
        okHttpClientBuilder.addInterceptor(headerInterceptor)


        return okHttpClientBuilder.build()
    }


    @Provides
    @Singleton
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            //hear you can add all headers you want by calling 'requestBuilder.addHeader(name ,  value)'
            it.proceed(requestBuilder.build())
        }
    }


    @Provides
    @Singleton
    internal fun provideCache(context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }


    @Provides
    @Singleton
    fun provideContext(application: App): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): ProfileApis {
        return retrofit.create(ProfileApis::class.java)
    }

    @Provides
    @Singleton
    fun provideGeneralApi(retrofit: Retrofit): GeneralApis {
        return retrofit.create(GeneralApis::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(profileApis: ProfileApis): ProfileRepository {
        return ProfileRepositoryImpl(apiService = profileApis)
    }

    @Provides
    @Singleton
    fun provideAppIntroRepository(generalApis: GeneralApis): AppIntroRepository {
        return AppIntroRepositoryImpl(apiService = generalApis)
    }






}