package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.ProfilePasswordRequest
import com.untillness.borwita.data.remote.requests.ProfileEditRequest
import okhttp3.MultipartBody

class ProfileRepository {
    suspend fun getProfile(token: String) = Api.getApiService(token).profile()

    suspend fun passwordEdit(
        token: String, req: ProfilePasswordRequest
    ) = Api.getApiService(token).passwordEdit(
        newPassword = req.newPassword, oldPassword = req.oldPassword
    )

    suspend fun profileEditPhoto(
        token: String,
        avatar: MultipartBody.Part,
    ) = Api.getApiService(token = token).profileEditPhoto(
        avatar
    )

    suspend fun profileEdit(
        token: String, registerRequest: ProfileEditRequest
    ) = Api.getApiService(token = token).profileEdit(
        email = registerRequest.email,
        name = registerRequest.name,
    )

}