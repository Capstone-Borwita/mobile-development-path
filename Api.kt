package com.untillness.borwita.data.remote.api

import com.untillness.borwita.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Kelas API untuk menyediakan Retrofit instance
class Api {

    companion object {
        // Fungsi untuk mendapatkan instans ApiInterface
        fun getApiService(token: String = ""): ApiInterface {
            // Interceptor untuk logging request/response (debugging)
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            // Interceptor untuk menambahkan header Authorization dengan token
            val authInterceptor = Interceptor { chain ->
                val req = chain.request()
                val requestHeaders = req
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token") // Tambah token jika tersedia
                    .build()

                chain.proceed(requestHeaders)
            }

            // OkHttpClient dengan logging dan auth interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Logging interceptor
                .addInterceptor(authInterceptor)    // Auth interceptor
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            // Retrofit builder
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_V1) // Base URL dari BuildConfig
                .addConverterFactory(GsonConverterFactory.create()) // Converter untuk JSON
                .client(client) // Tambahkan client yang sudah dikustomisasi
                .build()

            // Mengembalikan instans ApiInterface
            return retrofit.create(ApiInterface::class.java)
        }
    }
}