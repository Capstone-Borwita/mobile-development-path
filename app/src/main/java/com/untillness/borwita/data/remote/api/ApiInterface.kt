package com.untillness.borwita.data.remote.api

import com.untillness.borwita.data.remote.requests.LoginRequest
import com.untillness.borwita.data.remote.responses.BaseResponse
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.remote.responses.OcrResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.remote.responses.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiInterface {
    @Multipart
    @POST("/api/v1/auth/register")
    suspend fun register(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
    ): Response<RegisterResponse>

    @Multipart
    @POST("/api/v1/auth/login")
    suspend fun login(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
    ): Response<LoginResponse>

    @GET("/api/v1/auth/profile")
    suspend fun profile(): Response<ProfileResponse>

    @Multipart
    @PUT("/api/v1/auth/edit-password")
    suspend fun passwordEdit(
        @Part("old_password") oldPassword: RequestBody,
        @Part("new_password") newPassword: RequestBody,
    ): Response<BaseResponse>

    @Multipart
    @PUT("/api/v1/auth/edit-photo-profile")
    suspend fun profileEditPhoto(
        @Part avatar: MultipartBody.Part,
    ): Response<BaseResponse>

    @Multipart
    @PUT("/api/v1/auth/edit-profile")
    suspend fun profileEdit(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
    ): Response<BaseResponse>


    @GET("/reverse")
    suspend fun georeverse(
        @Query("format") format: String = "json",
        @Query("addressdetails") addressdetails: String = "1",
        @Query("lat") lat: String,
        @Query("lon") lon: String,
    ): Response<GeoreverseResponse>

    @Multipart
    @POST("/api/v1/ocr/ktp")
    suspend fun ocr(
        @Part ktp: MultipartBody.Part,
    ): Response<OcrResponse>
}
