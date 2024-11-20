package com.untillness.borwita.data.remote.api

import com.untillness.borwita.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Api {
    companion object {
        fun getApiService(token: String = ""): ApiInterface {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders =
                    req
                        .newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()

                chain.proceed(requestHeaders)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL_V1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

            return retrofit.create(ApiInterface::class.java)
        }
    }
}