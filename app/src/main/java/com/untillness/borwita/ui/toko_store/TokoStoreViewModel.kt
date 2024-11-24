package com.untillness.borwita.ui.toko_store

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.responses.DataOcr

class TokoStoreViewModel(context: Context) : ViewModel() {
    private val _selectedKtp: MutableLiveData<DataOcr> = MutableLiveData<DataOcr>()
    val selectedKtp: LiveData<DataOcr> = _selectedKtp

    fun assignSelectedKtp(newVal: DataOcr) {
        this._selectedKtp.value = newVal
    }
}