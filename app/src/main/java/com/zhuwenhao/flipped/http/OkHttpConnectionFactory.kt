package com.zhuwenhao.flipped.http

import com.zhuwenhao.flipped.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class OkHttpConnectionFactory private constructor() {

    companion object {
        private const val DEFAULT_TIMEOUT: Long = 30000

        private val CLIENT: OkHttpClient = createClient()

        fun getClient(): OkHttpClient {
            return CLIENT
        }

        private fun createClient(): OkHttpClient {
            val builder = OkHttpClient.Builder()
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)

            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }

            return builder.build()
        }
    }
}