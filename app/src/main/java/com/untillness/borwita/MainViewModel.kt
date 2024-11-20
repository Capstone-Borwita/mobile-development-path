package com.untillness.borwita

import android.app.Application
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.repositories.SharePrefRepository

class MainViewModel(application: Application) : ViewModel() {
    private val sharePrefRepository: SharePrefRepository = SharePrefRepository(application)

    fun isLogin(): Boolean {
        val token: String = this.sharePrefRepository.getToken()
        return token.isNotEmpty()
    }
}