package com.untillness.borwita.ui.wrapper.fragments.toko

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
import com.untillness.borwita.data.remote.responses.RegisterResponse
import com.untillness.borwita.data.remote.responses.TokoResponse
import com.untillness.borwita.data.states.AppState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TokoViewModel(context: Context) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()
    private val tokoRepository: TokoRepository = TokoRepository()

    private val _dataToko: MutableLiveData<AppState<List<DataToko>>> =
        MutableLiveData<AppState<List<DataToko>>>()
    val dataToko: LiveData<AppState<List<DataToko>>> = _dataToko

    init {
        loadDataToko(context)
    }

    fun refreshIndicator(context: Context) {
        this.loadDataToko(context)
    }

    fun loadDataToko(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._dataToko.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _dataToko.postValue(AppState.Loading)

            val response = async {
                tokoRepository.list(token = token)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _dataToko.postValue(
                    AppState.Error(
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }
            val tokoResponse: TokoResponse = response.body() ?: TokoResponse()

            if (tokoResponse.data?.isEmpty() ?: true) {
                _dataToko.postValue(
                    AppState.Error(
                        message = "Tidak ada data.",
                    )
                )
                return@launch
            }

            _dataToko.postValue(
                AppState.Success<List<DataToko>>(
                    message = "Berhasil",
                    data = tokoResponse.data?.filterNotNull() ?: listOf<DataToko>(),
                )
            )
        }
    }
}