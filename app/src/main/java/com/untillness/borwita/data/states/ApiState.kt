package com.untillness.borwita.data.states

sealed class ApiState {
    data object Standby : ApiState()

    data object Loading : ApiState()

    data class Error(
        val message: String
    ) : ApiState()

    data class Success<T>(
        val message: String,
        val data: T
    ) : ApiState()

}