package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class ProfileResponse(

	@field:SerializedName("data")
	val data: DataProfileResponse? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable

@Parcelize
data class DataProfileResponse(

	@field:SerializedName("avatar_path")
	val avatarPath: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null
) : Parcelable
