package com.untillness.borwita.data.remote.api

import com.untillness.borwita.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

<<<<<<< HEAD
class Api {
    companion object {
        fun getApiService(baseUrl: String, token: String = ""): ApiInterface {
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
                .addNetworkInterceptor { chain ->
                    chain.proceed(
                        chain.request()
                            .newBuilder()
                            .header("User-Agent", "BORWITA")
                            .build()
                    )
                }
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit
                .Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

            return retrofit.create(ApiInterface::class.java)
        }

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
=======
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
>>>>>>> 97dcba9 (Initial commit)

                chain.proceed(requestHeaders)
            }

<<<<<<< HEAD
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
=======
            // OkHttpClient dengan logging dan auth interceptor
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Logging interceptor
                .addInterceptor(authInterceptor)    // Auth interceptor
>>>>>>> 97dcba9 (Initial commit)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

<<<<<<< HEAD
            val retrofit = Retrofit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL_V1)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

=======
            // Retrofit builder
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL_V1) // Base URL dari BuildConfig
                .addConverterFactory(GsonConverterFactory.create()) // Converter untuk JSON
                .client(client) // Tambahkan client yang sudah dikustomisasi
                .build()

            // Mengembalikan instans ApiInterface
>>>>>>> 97dcba9 (Initial commit)
            return retrofit.create(ApiInterface::class.java)
        }
    }
}