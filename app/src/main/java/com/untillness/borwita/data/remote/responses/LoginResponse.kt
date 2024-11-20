package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class LoginResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("token")
	val token: String? = null
) : Parcelable
