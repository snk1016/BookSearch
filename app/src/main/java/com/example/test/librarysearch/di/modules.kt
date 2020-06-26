package com.example.test.librarysearch.di

import android.util.Log
import com.example.test.librarysearch.LibSearchApp
import com.example.test.librarysearch.services.NetworkService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single {
        Retrofit.Builder()
                .baseUrl((androidApplication() as LibSearchApp).serviceUrl)
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(NetworkService::class.java)
    }

    single {
        OkHttpClient.Builder().addInterceptor(get() as HttpLoggingInterceptor)
            .addInterceptor((androidApplication() as LibSearchApp).interceptor).build()
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}