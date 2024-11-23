package com.untillness.borwita.ui.capture

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.states.AppState

class CaptureViewModel: ViewModel() {
    private val _captureState: MutableLiveData<AppState<Uri>> = MutableLiveData()
    val captureState: LiveData<AppState<Uri>> = _captureState


}