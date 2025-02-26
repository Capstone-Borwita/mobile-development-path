package com.untillness.borwita.ui.profile_edit

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.ProfileRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.requests.ProfileEditRequest
import com.untillness.borwita.data.remote.responses.BaseResponse
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.helpers.FileHelper
import com.untillness.borwita.helpers.FileHelper.Companion.reduceFileImage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ProfileEditViewModel(context: Context) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    private val profileRepository: ProfileRepository = ProfileRepository()

    private val _profileEditPhotoState = MutableLiveData<AppState<BaseResponse?>>(AppState.Standby)
    val profileEditPhotoState: LiveData<AppState<BaseResponse?>> = _profileEditPhotoState

    private val _isUpdated = MutableLiveData<Boolean>(false)
    val isUpdated: LiveData<Boolean> = _isUpdated

    fun profileEditPhoto(context: Context, photo: Uri) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _profileEditPhotoState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _profileEditPhotoState.postValue(AppState.Loading)

            val imageFile = FileHelper.uriToFile(photo, context).reduceFileImage()

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val photoMultipartBody = MultipartBody.Part.createFormData(
                "avatar", imageFile.name, requestImageFile
            )

            val token: String = this@ProfileEditViewModel.token

            val response = async {
                profileRepository.profileEditPhoto(
                    token = token,
                    avatar = photoMultipartBody,
                )
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _profileEditPhotoState.postValue(
                    AppState.Error(
                        message = errorResponse.message ?: "",
                    )
                )
                return@launch
            }

            _isUpdated.postValue(true)

            _profileEditPhotoState.postValue(
                AppState.Success<BaseResponse?>(
                    message = "Berhasil mengubah foto profil.", data = null
                )
            )
        }
    }

    fun profileEdit(
        context: Context, profileEditRequest: ProfileEditRequest
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._profileEditPhotoState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _profileEditPhotoState.postValue(AppState.Loading)

            val response = async {
                profileRepository.profileEdit(
                    token, profileEditRequest
                )
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _profileEditPhotoState.postValue(
                    AppState.Error(
                        message = "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }

            _isUpdated.postValue(true)

            _profileEditPhotoState.postValue(
                AppState.Success(
                    message = "Berhasil menyimpan profil.",
                    data = null,
                )
            )
        }
    }
}