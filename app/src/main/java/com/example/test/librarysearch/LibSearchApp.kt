package com.example.test.librarysearch

import android.app.Application
import com.example.test.librarysearch.di.apiModule
import okhttp3.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LibSearchApp : Application() {

    lateinit var serviceUrl: String

    override fun onCreate() {
        super.onCreate()
        serviceUrl = getString(R.string.kakao_service_url)
        startKoin {
            androidContext(this@LibSearchApp)
            modules(apiModule)
        }
    }

    var interceptor = object: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder().header("Authorization", getString(R.string.kakao_authorization)).build()
            return chain.proceed(request)
        }
    }
}