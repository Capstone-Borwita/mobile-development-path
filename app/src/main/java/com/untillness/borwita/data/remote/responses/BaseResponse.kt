package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class BaseResponse(

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable
