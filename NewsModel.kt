package com.untillness.borwita.ui.berita

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.repositories.NewsRepository
import com.untillness.borwita.ui.wrapper.fragments.home.News
import kotlinx.coroutines.launch

class NewsModel : ViewModel() {
    private val _newsLiveData = MutableLiveData<List<News>>()
    val newsLiveData: LiveData<List<News>> get() = _newsLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    // Fungsi untuk mengambil data berita
    fun getNews() {
        // Menggunakan CoroutineScope untuk operasi asinkron
        viewModelScope.launch {
            try {
                val response = Api.getApiService().getAllNews() // Contoh panggilan API
                if (response.isSuccessful) {
                    _newsLiveData.postValue(response.body()) // Perbarui LiveData jika sukses
                } else {
                    _errorLiveData.postValue("Error: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Exception: ${e.message}")
            }
        }
    }

        val newslist: List<News> = listOf(
            News("Berita 1", "2024-11-26", R.drawable.news),
            News("Berita 2", "2024-11-25", R.drawable.news1),
            News("Berita 3", "2024-10-25", R.drawable.news2),
            // Tambahkan berita lainnya
        )
}



