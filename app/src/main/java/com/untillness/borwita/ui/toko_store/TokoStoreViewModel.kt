package com.untillness.borwita.ui.toko_store

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.GeoreverseResponse

class TokoStoreViewModel(context: Context) : ViewModel() {
    private val _selectedKtp: MutableLiveData<DataOcr> = MutableLiveData<DataOcr>()
    val selectedKtp: LiveData<DataOcr> = _selectedKtp

    private val _selectedToko: MutableLiveData<Uri> = MutableLiveData<Uri>()
    val selectedToko: LiveData<Uri> = _selectedToko

    private val _selectedMap: MutableLiveData<GeoreverseResponse> =
        MutableLiveData<GeoreverseResponse>()
    val selectedMap: LiveData<GeoreverseResponse> = _selectedMap

    fun assignSelectedKtp(newVal: DataOcr) {
        this._selectedKtp.value = newVal
    }

    fun assignSelectedToko(newVal: Uri) {
        this._selectedToko.value = newVal
    }

    fun assignSelectedMap(newVal: GeoreverseResponse) {
        this._selectedMap.value = newVal
    }
}