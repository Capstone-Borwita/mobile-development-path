package com.untillness.borwita.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.AuthRepository
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.RegisterResponse
<<<<<<< HEAD
import com.untillness.borwita.data.states.AppState
=======
import com.untillness.borwita.data.states.ApiState
>>>>>>> 97dcba9 (Initial commit)
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class RegisterViewModel : ViewModel() {
    private var authRepository: AuthRepository = AuthRepository()

<<<<<<< HEAD
    private val _registerState = MutableLiveData<AppState<RegisterResponse>>(AppState.Standby)
    val registerState: LiveData<AppState<RegisterResponse>> = _registerState
=======
    private val _registerState = MutableLiveData<ApiState<RegisterResponse>>(ApiState.Standby)
    val registerState: LiveData<ApiState<RegisterResponse>> = _registerState
>>>>>>> 97dcba9 (Initial commit)

    fun doRegister(
        context: Context,
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._registerState.postValue(
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
            _registerState.postValue(AppState.Loading)
=======
            _registerState.postValue(ApiState.Loading)
>>>>>>> 97dcba9 (Initial commit)

            val response = async {
                authRepository.register(
                    name,
                    email,
                    password,
                )
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _registerState.postValue(
<<<<<<< HEAD
                    AppState.Error(
=======
                    ApiState.Error(
>>>>>>> 97dcba9 (Initial commit)
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }
            val registerResponse: RegisterResponse = response.body() ?: RegisterResponse()

            _registerState.postValue(
<<<<<<< HEAD
                AppState.Success<RegisterResponse>(
=======
                ApiState.Success<RegisterResponse>(
>>>>>>> 97dcba9 (Initial commit)
                    message = "Berhasil",
                    data = registerResponse,
                )
            )
        }
    }
}