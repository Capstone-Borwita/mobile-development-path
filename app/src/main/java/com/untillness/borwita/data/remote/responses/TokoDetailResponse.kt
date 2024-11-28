package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class TokoDetailResponse(

	@field:SerializedName("data")
	val data: DataToko? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable
