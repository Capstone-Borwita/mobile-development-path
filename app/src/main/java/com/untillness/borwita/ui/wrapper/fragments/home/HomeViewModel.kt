package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.NewsRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.repositories.TokoRepository
import com.untillness.borwita.data.remote.responses.DataNewsResponse
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.NewsResponse
import com.untillness.borwita.data.states.AppState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel(
    context: Context,
) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()
    private val newsRepository: NewsRepository = NewsRepository()

    private val _dataNews: MutableLiveData<AppState<List<DataNewsResponse>>> =
        MutableLiveData<AppState<List<DataNewsResponse>>>()
    val dataNews: LiveData<AppState<List<DataNewsResponse>>> = _dataNews

    init {
        loadDataNews(context)
    }

    fun removeToken() {
        return sharePrefRepository.removeToken()
    }

    fun refreshIndicator(context: Context) {
        this.loadDataNews(context)
    }

    fun loadDataNews(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._dataNews.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _dataNews.postValue(AppState.Loading)

            val response = async {
                newsRepository.list(token = token)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _dataNews.postValue(
                    AppState.Error(
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }
            val tokoResponse: NewsResponse = response.body() ?: NewsResponse()

            if (tokoResponse.data?.isEmpty() ?: true) {
                _dataNews.postValue(
                    AppState.Error(
                        message = "Tidak ada data.",
                    )
                )
                return@launch
            }

            _dataNews.postValue(
                AppState.Success<List<DataNewsResponse>>(
                    message = "Berhasil",
                    data = tokoResponse.data?.filterNotNull() ?: listOf<DataNewsResponse>(),
                )
            )
        }
    }

}