package com.untillness.borwita.ui.toko_detail

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.repositories.TokoRepository
import com.untillness.borwita.data.remote.responses.DataToko
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.TokoDetailResponse
import com.untillness.borwita.data.remote.responses.TokoResponse
import com.untillness.borwita.data.states.AppState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TokoDetailViewModel(context: Context) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()
    private val tokoRepository: TokoRepository = TokoRepository()

    private val _detailTokoState: MutableLiveData<AppState<DataToko>> =
        MutableLiveData<AppState<DataToko>>(AppState.Loading)
    val detailTokoState: LiveData<AppState<DataToko>> = _detailTokoState

    var id: String = ""


    fun loadData(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._detailTokoState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _detailTokoState.postValue(AppState.Loading)

            val response = async {
                tokoRepository.detail(token = token, id = this@TokoDetailViewModel.id)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _detailTokoState.postValue(
                    AppState.Error(
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }
            val tokoResponse: TokoDetailResponse = response.body() ?: TokoDetailResponse()

            _detailTokoState.postValue(
                AppState.Success<DataToko>(
                    message = "Berhasil",
                    data = tokoResponse.data ?: DataToko(),
                )
            )
        }
    }
}