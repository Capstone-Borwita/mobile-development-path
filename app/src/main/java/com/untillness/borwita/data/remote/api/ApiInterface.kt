package com.untillness.borwita.data.remote.api

import com.untillness.borwita.data.remote.requests.LoginRequest
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.remote.responses.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
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

//    @GET("/v1/stories")
//    suspend fun getAllStoriesWithLocation(
//        @Query("location") location: Int = 1
//    ): Response<StoryResponse>
//
//    @GET("/v1/stories")
//    suspend fun getAllStories(
//        @Query("page") page: Int = 1,
//        @Query("size") size: Int = 20,
//    ): Response<StoryResponse>
//
//    @Multipart
//    @POST("/v1/stories")
//    suspend fun storeStory(
//        @Part photo: MultipartBody.Part,
//        @Part("description") description: RequestBody,
//    ): Response<ErrorResponse>
//
//    @Multipart
//    @POST("/v1/stories")
//    suspend fun storeStory(
//        @Part photo: MultipartBody.Part,
//        @Part("description") description: RequestBody,
//        @Part("lat") lat: RequestBody?,
//        @Part("lon") lon: RequestBody?,
//    ): Response<ErrorResponse>
}
