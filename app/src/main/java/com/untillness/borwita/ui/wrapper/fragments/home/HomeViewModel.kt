package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
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

class HomeViewModel(
    context: Context,
) : ViewModel() {
    private val profileRepository: ProfileRepository = ProfileRepository()

    private val _profileState = MutableLiveData<ApiState<ProfileResponse>>(ApiState.Loading)
    val profileState: LiveData<ApiState<ProfileResponse>> = _profileState

    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    fun removeToken() {
        return sharePrefRepository.removeToken()
    }

    fun loadProfile(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _profileState.postValue(
                ApiState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _profileState.postValue(ApiState.Loading)

            val response = async {
                profileRepository.getProfile(token)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _profileState.postValue(
                    ApiState.Error(
                        message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi),
                    )
                )
                return@launch
            }

            val profileResponse: ProfileResponse = response.body() ?: ProfileResponse()

            _profileState.postValue(
                ApiState.Success(
                    data = profileResponse,
                    message = "Berhasil"
                )
            )
        }
    }
}