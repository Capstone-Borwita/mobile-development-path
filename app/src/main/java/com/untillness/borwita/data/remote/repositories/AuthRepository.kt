package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.LoginRequest
import okhttp3.RequestBody

class AuthRepository {
    suspend fun register(
        name: RequestBody, email: RequestBody, password: RequestBody,
    ) = Api.getApiService().register(
        name,
        email,
        password,
    )

    suspend fun login(req: LoginRequest) = Api.getApiService().login(
        req
    )
}