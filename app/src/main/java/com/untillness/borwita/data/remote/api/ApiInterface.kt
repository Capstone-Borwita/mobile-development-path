package com.untillness.borwita.data.remote.api

import com.untillness.borwita.data.remote.requests.LoginRequest
import com.untillness.borwita.data.remote.responses.BaseResponse
import com.untillness.borwita.data.remote.responses.ErrorResponse
<<<<<<< HEAD
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.remote.responses.OcrResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.remote.responses.RegisterResponse
import com.untillness.borwita.data.remote.responses.TokoDetailResponse
import com.untillness.borwita.data.remote.responses.TokoResponse
=======
import com.untillness.borwita.data.remote.responses.LoginResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.remote.responses.RegisterResponse
import com.untillness.borwita.ui.wrapper.fragments.home.News
>>>>>>> 97dcba9 (Initial commit)
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
<<<<<<< HEAD
import retrofit2.http.DELETE
=======
>>>>>>> 97dcba9 (Initial commit)
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
<<<<<<< HEAD
import retrofit2.http.Path
=======
>>>>>>> 97dcba9 (Initial commit)
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

<<<<<<< HEAD

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

    @Multipart
    @POST("/api/v1/stores")
    suspend fun storeToko(
        @Part("name") name: RequestBody,
        @Part("owner_name") ownerName: RequestBody,
        @Part("keeper_phone_number") phone: RequestBody,
        @Part("keeper_nik") keeperNik: RequestBody,
        @Part("keeper_name") keeperName: RequestBody,
        @Part("keeper_address") keeperAddress: RequestBody,
        @Part("latitude") lat: RequestBody,
        @Part("longitude") long: RequestBody,
        @Part("georeverse") address: RequestBody,
        @Part("ktp_photo_identifier") ktp: RequestBody,
        @Part storePhoto: MultipartBody.Part,
    ): Response<BaseResponse>

    @GET("/api/v1/stores/")
    suspend fun listToko(): Response<TokoResponse>

    @GET("/api/v1/stores/{id}")
    suspend fun detailToko(
        @Path("id") id: String,
    ): Response<TokoDetailResponse>

    @DELETE("/api/v1/stores/{id}")
    suspend fun deleteToko(
        @Path("id") id: String,
    ): Response<BaseResponse>
}
=======
    // ---- API untuk Berita ----

    // Mengambil semua berita
    @GET("/api/v1/news")
    suspend fun getAllNews(): Response<List<News>>

    // Menambahkan berita baru
    @Multipart
    @POST("/api/v1/news")
    suspend fun createNews(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("author") author: RequestBody,
    ): Response<BaseResponse>

}
>>>>>>> 97dcba9 (Initial commit)
