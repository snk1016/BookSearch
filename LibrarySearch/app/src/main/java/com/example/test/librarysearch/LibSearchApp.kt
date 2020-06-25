package com.example.test.librarysearch

import android.app.Application
import com.example.test.librarysearch.di.apiModule
import com.example.test.librarysearch.services.NetworkService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LibSearchApp : Application() {

    private val CONNECT_TIMEOUT = 10L
    private val WRITE_TIMEOUT = 1L
    private val READ_TIMEOUT = 20L

    lateinit var serviceUrl: String

    override fun onCreate() {
        super.onCreate()
        serviceUrl = getString(R.string.kakao_service_url)
        startKoin { apiModule }
    }

    var interceptor = object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            request.newBuilder().addHeader("Authorization", getString(R.string.kakao_authorization))

            return chain.proceed(request)
        }
    }
}