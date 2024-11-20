package com.untillness.borwita.ui.profile_password

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.ProfileRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.requests.ProfilePasswordRequest
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.states.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProfilePasswordViewModel(context: Context) : ViewModel() {
    private var profileRepository: ProfileRepository = ProfileRepository()

    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    private val _passwordEditState = MutableLiveData<ApiState<Nothing?>>(ApiState.Standby)
    val passwordEditState: LiveData<ApiState<Nothing?>> = _passwordEditState

    fun doPasswordEdit(
        context: Context, req: ProfilePasswordRequest
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._passwordEditState.postValue(
                ApiState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _passwordEditState.postValue(ApiState.Loading)

            val response = async {
                profileRepository.passwordEdit(token = token, req = req)
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _passwordEditState.postValue(
                    ApiState.Error(
                        message = "Kata Sandi Lama yang kamu masukkan tidak sesuai, silahkan coba lagi.",
                    )
                )
                return@launch
            }

            _passwordEditState.postValue(
                ApiState.Success<Nothing?>(
                    message = "Berhasil mengubah kata sandi.",
                    data = null,
                )
            )
        }
    }
}