package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.ProfilePasswordRequest
import com.untillness.borwita.data.remote.requests.ProfileEditRequest
import okhttp3.MultipartBody

class NewsRepository {
    suspend fun list(token: String) = Api.getApiService(token).news()

}