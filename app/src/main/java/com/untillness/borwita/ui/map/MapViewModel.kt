package com.untillness.borwita.ui.map

import android.content.Context
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.states.ApiState
import com.untillness.borwita.helpers.AppHelpers
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _mapApiState: MutableLiveData<ApiState<GeoreverseResponse>> =
        MutableLiveData(ApiState.Standby)
    val mapApiState: LiveData<ApiState<GeoreverseResponse>> = _mapApiState

    private val jakartaLatLng = LatLng(-6.1758237, 106.8262149)
    private val _currentLatLong: MutableLiveData<LatLng> = MutableLiveData(jakartaLatLng)
    val currentLatLong: LiveData<LatLng> = _currentLatLong

    var displayMap = ""

    fun setLoadingState() {
        this@MapViewModel._mapApiState.postValue(ApiState.Loading)
    }

    fun setCurrentLatLong(value: LatLng) {
        this._currentLatLong.postValue(value)
    }

    fun getGeoreverse(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _mapApiState.postValue(
                ApiState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            val places = async {
                Api.getApiService(
                    baseUrl = "https://nominatim.openstreetmap.org/"
                ).georeverse(
                    lat = this@MapViewModel._currentLatLong.value?.latitude.toString(),
                    lon = this@MapViewModel._currentLatLong.value?.longitude.toString(),
                )
            }.await()

            this@MapViewModel.displayMap = places.body()?.displayName ?: ""
            this@MapViewModel._mapApiState.postValue(
                ApiState.Success(
                    message = "Berhasil", data = places.body() ?: GeoreverseResponse()
                )
            )
        }
    }
}