package com.untillness.borwita.data.remote.repositories

import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.remote.requests.LoginRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

class TokoRepository {
    suspend fun detail(
        token: String,
        id: String,
    ) = Api.getApiService(token).detailToko(id)

    suspend fun list(
        token: String,
    ) = Api.getApiService(token).listToko()

    suspend fun ocr(
        token: String,
        photo: MultipartBody.Part,
    ) = Api.getApiService(token).ocr(
        photo
    )

    suspend fun store(
        token: String,
        name: RequestBody,
        ownerName: RequestBody,
        phone: RequestBody,
        keeperNik: RequestBody,
        keeperName: RequestBody,
        keeperAddress: RequestBody,
        lat: RequestBody,
        long: RequestBody,
        address: RequestBody,
        ktp: RequestBody,
        storePhoto: MultipartBody.Part,
    ) = Api.getApiService(token).storeToko(
        name,
        ownerName,
        phone,
        keeperNik,
        keeperName,
        keeperAddress,
        lat,
        long,
        address,
        ktp,
        storePhoto,
    )
}