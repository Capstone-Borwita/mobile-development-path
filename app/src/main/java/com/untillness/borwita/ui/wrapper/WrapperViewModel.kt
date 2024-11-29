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
<<<<<<< HEAD
import com.untillness.borwita.data.states.AppState
=======
import com.untillness.borwita.data.states.ApiState
>>>>>>> 97dcba9 (Initial commit)
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

<<<<<<< HEAD
    private val _profileState = MutableLiveData<AppState<ProfileResponse>>(AppState.Loading)
    val profileState: LiveData<AppState<ProfileResponse>> = _profileState
=======
    private val _profileState = MutableLiveData<ApiState<ProfileResponse>>(ApiState.Loading)
    val profileState: LiveData<ApiState<ProfileResponse>> = _profileState
>>>>>>> 97dcba9 (Initial commit)

    init {
        this.initState(context)
    }

    fun initState(context: Context) {
        this.loadProfile(context)
    }

    private fun loadProfile(context: Context) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _profileState.postValue(
<<<<<<< HEAD
                AppState.Error(
=======
                ApiState.Error(
>>>>>>> 97dcba9 (Initial commit)
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
<<<<<<< HEAD
            _profileState.postValue(AppState.Loading)
=======
            _profileState.postValue(ApiState.Loading)
>>>>>>> 97dcba9 (Initial commit)

            val response = async {
                profileRepository.getProfile(token)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _profileState.postValue(
<<<<<<< HEAD
                    AppState.Error(
=======
                    ApiState.Error(
>>>>>>> 97dcba9 (Initial commit)
                        message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi),
                    )
                )
                return@launch
            }

            val profileResponse: ProfileResponse = response.body() ?: ProfileResponse()

            _profileState.postValue(
<<<<<<< HEAD
                AppState.Success(
=======
                ApiState.Success(
>>>>>>> 97dcba9 (Initial commit)
                    data = profileResponse, message = "Berhasil"
                )
            )
        }
    }
}