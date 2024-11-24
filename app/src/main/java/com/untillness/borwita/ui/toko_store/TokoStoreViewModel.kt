package com.untillness.borwita.ui.toko_store

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.responses.DataOcr

class TokoStoreViewModel(context: Context) : ViewModel() {
    private val _selectedKtp: MutableLiveData<DataOcr> = MutableLiveData<DataOcr>()
    val selectedKtp: LiveData<DataOcr> = _selectedKtp

    private val _selectedToko: MutableLiveData<Uri> = MutableLiveData<Uri>()
    val selectedToko: LiveData<Uri> = _selectedToko

    fun assignSelectedKtp(newVal: DataOcr) {
        this._selectedKtp.value = newVal
    }

    fun assignSelectedToko(newVal: Uri) {
        this._selectedToko.value = newVal
    }
}