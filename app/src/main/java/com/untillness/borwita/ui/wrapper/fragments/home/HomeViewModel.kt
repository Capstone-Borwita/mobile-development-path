package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
<<<<<<< HEAD
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
=======
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.AuthRepository
import com.untillness.borwita.data.remote.repositories.ProfileRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.states.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
>>>>>>> 97dcba9 (Initial commit)

class HomeViewModel(
    context: Context,
) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )

<<<<<<< HEAD
    fun removeToken() {
        return sharePrefRepository.removeToken()
    }
}
=======
    private val _headlineNews = MutableLiveData<News>()
    val headlineNews: LiveData<News> get() = _headlineNews

    private val _carouselNews = MutableLiveData<List<News>>()
    val carouselNews: LiveData<List<News>> get() = _carouselNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun loadData() {
        _isLoading.value = true
        // Simulate data fetching
        _headlineNews.value = News("Headline Title", "Monday, 17 August 2025", R.drawable.news)
        _carouselNews.value = listOf(
            News("News 1", "Date 1", R.drawable.news),
            News("News 2", "Date 2", R.drawable.news1),
            News("News 3", "Date 3", R.drawable.news2)
        )
        _isLoading.value = false
    }

    fun refreshData() {
        loadData()
    }

    fun removeToken() {
        return sharePrefRepository.removeToken()
    }
}

data class News(
    val title: String,
    val date: String,
    val photo: Int
)
>>>>>>> 97dcba9 (Initial commit)
