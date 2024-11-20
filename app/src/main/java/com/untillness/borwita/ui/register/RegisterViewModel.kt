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
import com.untillness.borwita.data.states.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class RegisterViewModel : ViewModel() {
    private var authRepository: AuthRepository = AuthRepository()

    private val _registerState = MutableLiveData<ApiState>(ApiState.Standby)
    val registerState: LiveData<ApiState> = _registerState

    fun doRegister(
        context: Context,
        name: RequestBody,
        email: RequestBody,
        password: RequestBody,
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._registerState.postValue(
                ApiState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _registerState.postValue(ApiState.Loading)

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
                    ApiState.Error(
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }
            val registerResponse: RegisterResponse = response.body() ?: RegisterResponse()

            _registerState.postValue(
                ApiState.Success<RegisterResponse>(
                    message = "Berhasil",
                    data = registerResponse,
                )
            )
        }
    }
}