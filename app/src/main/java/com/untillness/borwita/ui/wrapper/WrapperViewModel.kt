package com.untillness.borwita.ui.wrapper

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.ProfileRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.states.AppState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WrapperViewModel(
    context: Context,
) : ViewModel() {
    private val profileRepository: ProfileRepository = ProfileRepository()

    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    private val _profileState = MutableLiveData<AppState<ProfileResponse>>(AppState.Loading)
    val profileState: LiveData<AppState<ProfileResponse>> = _profileState

    init {
        this.initState(context)
    }

    fun initState(context: Context) {
        this.loadProfile(context)
    }

    private fun loadProfile(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _profileState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _profileState.postValue(AppState.Loading)

            val response = async {
                profileRepository.getProfile(token)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _profileState.postValue(
                    AppState.Error(
                        message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi),
                    )
                )
                return@launch
            }

            val profileResponse: ProfileResponse = response.body() ?: ProfileResponse()

            _profileState.postValue(
                AppState.Success(
                    data = profileResponse, message = "Berhasil"
                )
            )
        }
    }
}