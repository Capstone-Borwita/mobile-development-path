package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class TokoResponse(

	@field:SerializedName("data")
	val data: List<DataToko?>? = null,

	@field:SerializedName("detail")
	val detail: String? = null
) : Parcelable

@Parcelize
data class DataToko(

	@field:SerializedName("code")
	val code: String? = null,

	@field:SerializedName("owner_name")
	val ownerName: String? = null,

	@field:SerializedName("keeper_address")
	val keeperAddress: String? = null,

	@field:SerializedName("keeper_name")
	val keeperName: String? = null,

	@field:SerializedName("latitude")
	val latitude: String? = null,

	@field:SerializedName("keeper_phone_number")
	val keeperPhoneNumber: String? = null,

	@field:SerializedName("georeverse")
	val georeverse: String? = null,

	@field:SerializedName("keeper_nik")
	val keeperNik: String? = null,

	@field:SerializedName("ktp_photo_path")
	val ktpPhotoPath: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("store_photo_path")
	val storePhotoPath: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("longitude")
	val longitude: String? = null
) : Parcelable
