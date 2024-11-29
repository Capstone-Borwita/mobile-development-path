package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.ui.wrapper.fragments.home.News

class NewsRepository {

    private val apiService = Api.getApiService()

    suspend fun getAllNews(): List<News>? {
        return try {
            val response = apiService.getAllNews()
            if (response.isSuccessful) {
                response.body()
            } else {
                // Menangani error jika API memberi respons gagal
                null
            }
        } catch (e: Exception) {
            // Menangani error seperti kesalahan jaringan
            null
        }
    }
}
