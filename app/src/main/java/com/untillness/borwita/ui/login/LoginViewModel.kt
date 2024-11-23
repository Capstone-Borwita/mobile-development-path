package com.untillness.borwita.ui.login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.AuthRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.requests.LoginRequest
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.states.AppState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : ViewModel() {
    private val authRepository: AuthRepository = AuthRepository()
    private val sharePrefRepository: SharePrefRepository = SharePrefRepository(application)

    private val _loginState = MutableLiveData<AppState<LoginResponse>>(AppState.Standby)
    val loginState: LiveData<AppState<LoginResponse>> = _loginState

    private fun storeToken(token: String) {
        sharePrefRepository.setToken(
            token,
        )
    }

    private fun storeName(name: String) {
        sharePrefRepository.setName(
            name,
        )
    }

    fun doLogin(context: Context, request: LoginRequest) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _loginState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _loginState.postValue(AppState.Loading)

            val response = async {
                authRepository.login(request)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _loginState.postValue(
                    AppState.Error(
                        message = "Email atau Kata Sandi yang kamu masukkan salah, silakan coba lagi.",
                    )
                )
                return@launch
            }

            val loginResponse: LoginResponse = response.body() ?: LoginResponse()

            this@LoginViewModel.afterLoginSuccess(
                loginResponse = loginResponse
            )

            _loginState.postValue(
                AppState.Success(
                    data = loginResponse,
                    message = "Berhasil Login"
                )
            )
        }
    }


    private fun afterLoginSuccess(loginResponse: LoginResponse) {
        this.storeToken(
            loginResponse.data?.token ?: ""
        )
//        this.storeName(
//            loginResponse.loginResult?.name ?: ""
//        )
    }
}