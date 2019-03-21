package com.willowtreeapps.willowbrew.api.di

import android.content.SharedPreferences
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.willowtreeapps.willowbrew.api.moshiadapters.ApiJsonAdapter
import com.willowtreeapps.willowbrew.api.moshiadapters.EnumAdapter
import com.squareup.moshi.Moshi
import com.willowtreeapps.willowbrew.api.WillowBrewApi
import com.willowtreeapps.willowbrew.api.utils.SafeLoggingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApiModule(prefs: SharedPreferences, private val baseUrl: String) {
//
//    private val sessionStore = SessionStore(prefs)
//
//    @Provides
//    @Singleton
//    fun provideSessionStore(): SessionStore = sessionStore
//
    @Provides
    @Singleton
    @Named("log")
    fun provideLogInterceptor(): Interceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return SafeLoggingInterceptor(interceptor)
    }
//
//    @Provides
//    @Singleton
//    @Named("auth")
//    fun provideAuthInterceptor(
//            sessionStore: SessionStore
//    ): Interceptor = AuthHeaderInterceptor(sessionStore)

    @Provides
    @Singleton
    fun provideHttpClient(
            @Named("log") loggingInterceptor: Interceptor
//            @Named("auth") authInterceptor: Interceptor
    ) = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(authInterceptor)
            .readTimeout(6, TimeUnit.MINUTES)
            .connectTimeout(6, TimeUnit.MINUTES)
            .writeTimeout(6, TimeUnit.MINUTES)
            .build()

    @Provides
    @Singleton
    fun provideMoshi() = Moshi.Builder()
            .add(ApiJsonAdapter.FACTORY)
            .add(EnumAdapter.FACTORY)

            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
            moshi: Moshi,
            okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): WillowBrewApi = retrofit.create(WillowBrewApi::class.java)
//
//    @Provides
//    @Singleton
//    fun provideRequestFactory(api: ServiceNetApi, moshi: Moshi) = RequestFactory(api, moshi)
}
