package com.untillness.borwita.data.states

sealed class ApiState<out T> {
    data object Standby : ApiState<Nothing>()

    data object Loading : ApiState<Nothing>()

    data class Error(
        val message: String
    ) : ApiState<Nothing>()

    data class Success<T>(
        val message: String, val data: T
    ) : ApiState<T>()

}