package com.untillness.borwita.ui.wrapper.fragments.profile

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.repositories.SharePrefRepository

class ProfileViewModel(
    context: Context,
    injectSp: SharedPreferences? = null,
) : ViewModel() {

    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
        injectSp = injectSp,
    )

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text


    fun removeToken() {
        return sharePrefRepository.removeToken()
    }
}