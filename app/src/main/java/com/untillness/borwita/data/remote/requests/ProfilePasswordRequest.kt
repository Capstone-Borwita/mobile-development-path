package com.untillness.borwita.data.remote.requests

import okhttp3.RequestBody

data class ProfilePasswordRequest(
    val newPassword: RequestBody,
    val oldPassword: RequestBody,
)
