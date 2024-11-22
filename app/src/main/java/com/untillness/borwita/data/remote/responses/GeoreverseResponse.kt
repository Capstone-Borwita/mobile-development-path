package com.untillness.borwita.data.remote.responses

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class GeoreverseResponse(

	@field:SerializedName("osm_id")
	val osmId: Int? = null,

	@field:SerializedName("place_rank")
	val placeRank: Int? = null,

	@field:SerializedName("licence")
	val licence: String? = null,

	@field:SerializedName("boundingbox")
	val boundingbox: List<String?>? = null,

	@field:SerializedName("address")
	val address: Address? = null,

	@field:SerializedName("lon")
	val lon: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("display_name")
	val displayName: String? = null,

	@field:SerializedName("osm_type")
	val osmType: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("addresstype")
	val addresstype: String? = null,

	@field:SerializedName("class")
	val jsonMemberClass: String? = null,

	@field:SerializedName("place_id")
	val placeId: Int? = null,

	@field:SerializedName("lat")
	val lat: String? = null
) : Parcelable

@Parcelize
data class Address(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("country_code")
	val countryCode: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("neighbourhood")
	val neighbourhood: String? = null,

	@field:SerializedName("ISO3166-2-lvl3")
	val iSO31662Lvl3: String? = null,

	@field:SerializedName("ISO3166-2-lvl4")
	val iSO31662Lvl4: String? = null,

	@field:SerializedName("postcode")
	val postcode: String? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("village")
	val village: String? = null,

	@field:SerializedName("city_district")
	val cityDistrict: String? = null,

	@field:SerializedName("region")
	val region: String? = null
) : Parcelable
