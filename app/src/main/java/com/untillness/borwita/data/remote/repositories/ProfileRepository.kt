package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api

class ProfileRepository {
    suspend fun getProfile(token: String) = Api.getApiService(token).profile()
}