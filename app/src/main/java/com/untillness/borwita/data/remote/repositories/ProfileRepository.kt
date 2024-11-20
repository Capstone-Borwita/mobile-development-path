package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.ProfilePasswordRequest

class ProfileRepository {
    suspend fun getProfile(token: String) = Api.getApiService(token).profile()

    suspend fun passwordEdit(
        token: String,
        req: ProfilePasswordRequest
    ) = Api.getApiService(token).passwordEdit(
        newPassword = req.newPassword,
        oldPassword = req.oldPassword
    )
}