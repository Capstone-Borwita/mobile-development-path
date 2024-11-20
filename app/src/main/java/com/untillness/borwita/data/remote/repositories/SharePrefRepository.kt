package com.untillness.borwita.data.remote.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class SharePrefRepository(context: Context, injectSp: SharedPreferences? = null) {
    companion object {
        const val SP_TOKEN = "SP_TOKEN"
        const val SP_NAME = "SP_NAME"
    }

    private var sp: SharedPreferences =
        injectSp ?: PreferenceManager.getDefaultSharedPreferences(context)

    fun getToken(): String {
        return this.sp.getString(SP_TOKEN, "") ?: ""
    }

    fun setToken(token: String) {
        sp.edit().apply {
            putString(SP_TOKEN, token)
        }.apply()
    }

    fun getName(): String {
        return this.sp.getString(SP_NAME, "") ?: ""
    }

    fun setName(token: String) {
        sp.edit().apply {
            putString(SP_NAME, token)
        }.apply()
    }

    fun removeName() {
        setName("")
    }

    fun removeToken() {
        setToken("")
    }
}