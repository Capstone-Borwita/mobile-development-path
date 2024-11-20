package com.untillness.borwita.data.remote.requests

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody

data class LoginRequest(
    val password: RequestBody,

    val email: RequestBody,
)
