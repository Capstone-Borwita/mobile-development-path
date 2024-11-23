package com.untillness.borwita.ui.wrapper.fragments.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.repositories.SharePrefRepository

class HomeViewModel(
    context: Context,
) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )

    fun removeToken() {
        return sharePrefRepository.removeToken()
    }
}