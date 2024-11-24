package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.LoginRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody

class TokoRepository {
    suspend fun ocr(
        token: String,
        photo: MultipartBody.Part,
    ) = Api.getApiService(token).ocr(
        photo
    )
}