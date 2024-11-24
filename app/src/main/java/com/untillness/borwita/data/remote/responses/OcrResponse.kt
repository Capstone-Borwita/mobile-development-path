package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class OcrResponse(

	@field:SerializedName("data")
	val data: DataOcr? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable

@Parcelize
data class DataOcr(

	@field:SerializedName("identifier")
	val identifier: String? = null,

	@field:SerializedName("nik")
	val nik: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("local_path")
	val localPath: String? = null
) : Parcelable
