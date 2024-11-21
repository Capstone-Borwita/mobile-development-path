package com.untillness.borwita.data.remote.requests

import okhttp3.RequestBody

data class ProfileEditRequest(
    val name: RequestBody,
    val email: RequestBody,
)
