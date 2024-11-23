package com.untillness.borwita.data.states

sealed class AppState<out T> {
    data object Standby : AppState<Nothing>()

    data object Loading : AppState<Nothing>()

    data class Error(
        val message: String
    ) : AppState<Nothing>()

    data class Success<T>(
        val message: String, val data: T
    ) : AppState<T>()

}