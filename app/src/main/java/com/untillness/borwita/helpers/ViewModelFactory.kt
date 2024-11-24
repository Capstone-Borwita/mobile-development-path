package com.untillness.borwita.helpers

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.untillness.borwita.MainViewModel
import com.untillness.borwita.ui.capture.CaptureViewModel
import com.untillness.borwita.ui.login.LoginViewModel
import com.untillness.borwita.ui.map.MapViewModel
import com.untillness.borwita.ui.profile_edit.ProfileEditViewModel
import com.untillness.borwita.ui.profile_password.ProfilePasswordViewModel
import com.untillness.borwita.ui.register.RegisterViewModel
import com.untillness.borwita.ui.wrapper.WrapperViewModel
import com.untillness.borwita.ui.wrapper.fragments.home.HomeViewModel
import com.untillness.borwita.ui.wrapper.fragments.profile.ProfileViewModel

class ViewModelFactory private constructor(private val mApplication: Application) :
    ViewModelProvider.NewInstanceFactory() {
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(application: Application?): ViewModelFactory {
            if (application == null) return INSTANCE as ViewModelFactory

            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }
            return INSTANCE as ViewModelFactory
        }

        inline fun <reified T : ViewModel> obtainViewModel(activity: AppCompatActivity): T {
            val factory = getInstance(activity.application)
            return ViewModelProvider(activity, factory)[T::class.java]
        }
        inline fun <reified T : ViewModel> obtainViewModel(activity: FragmentActivity): T {
            val factory = getInstance(activity.application)
            return ViewModelProvider(activity, factory)[T::class.java]
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel() as T
        }
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(ProfilePasswordViewModel::class.java)) {
            return ProfilePasswordViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(WrapperViewModel::class.java)) {
            return WrapperViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(ProfileEditViewModel::class.java)) {
            return ProfileEditViewModel(mApplication) as T
        }
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel() as T
        }
        if (modelClass.isAssignableFrom(CaptureViewModel::class.java)) {
            return CaptureViewModel(mApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}