package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class NewsResponse(

	@field:SerializedName("data")
	val data: List<DataNewsResponse?>? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable

@Parcelize
data class DataNewsResponse(
	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("author_id")
	val authorId: Int? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("poster")
	val poster: String? = null,

	@field:SerializedName("content")
	val content: String? = null
) : Parcelable
