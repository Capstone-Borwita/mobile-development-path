package com.untillness.borwita.ui.wrapper.fragments.profile

import android.content.Context
<<<<<<< HEAD
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
=======
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.ProfileRepository
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.ProfileResponse
import com.untillness.borwita.data.states.ApiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
>>>>>>> 97dcba9 (Initial commit)

class ProfileViewModel(
    context: Context,
) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )

    fun removeToken() {
        return sharePrefRepository.removeToken()
    }
}