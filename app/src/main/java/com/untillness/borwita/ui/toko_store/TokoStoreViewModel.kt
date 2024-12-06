package com.untillness.borwita.ui.toko_store

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.repositories.TokoRepository
import com.untillness.borwita.data.remote.requests.ProfileEditRequest
import com.untillness.borwita.data.remote.responses.BaseResponse
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.FileHelper
import com.untillness.borwita.helpers.FileHelper.Companion.reduceFileImage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.jetbrains.annotations.ApiStatus

class TokoStoreViewModel(context: Context) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    private val tokoRepository: TokoRepository = TokoRepository()

    private val _selectedKtp: MutableLiveData<DataOcr> = MutableLiveData<DataOcr>()
    val selectedKtp: LiveData<DataOcr> = _selectedKtp

    private val _selectedToko: MutableLiveData<Uri> = MutableLiveData<Uri>()
    val selectedToko: LiveData<Uri> = _selectedToko

    private val _selectedMap: MutableLiveData<GeoreverseResponse> =
        MutableLiveData<GeoreverseResponse>()
    val selectedMap: LiveData<GeoreverseResponse> = _selectedMap

    private val _storeTokoState: MutableLiveData<AppState<BaseResponse>> =
        MutableLiveData<AppState<BaseResponse>>()
    val storeTokoState: LiveData<AppState<BaseResponse>> = _storeTokoState

    private val _ocrState: MutableLiveData<AppState<DataOcr>> = MutableLiveData(AppState.Standby)
    val ocrState: LiveData<AppState<DataOcr>> = _ocrState
    lateinit var capturedImageKtp: Uri

    fun assignSelectedKtp(newVal: DataOcr) {
        this._selectedKtp.value = newVal
    }

    fun assignSelectedToko(newVal: Uri) {
        this._selectedToko.value = newVal
    }

    fun assignSelectedMap(newVal: GeoreverseResponse) {
        this._selectedMap.value = newVal
    }

    fun store(
        context: Context,
        name: String,
        ownerName: String,
        phone: String,
        keeperName: String,
        keeperAddress: String,
        nik: String,
    ) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            this._storeTokoState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _storeTokoState.postValue(AppState.Loading)

            val nameRequestBody: RequestBody = AppHelpers.makeRequestBody(name)
            val ownerNameRequestBody: RequestBody = AppHelpers.makeRequestBody(ownerName)
            val phoneRequestBody: RequestBody = AppHelpers.makeRequestBody(phone)

            val ktpRequestBody: RequestBody =
                AppHelpers.makeRequestBody(this@TokoStoreViewModel._selectedKtp.value?.identifier)
            val keeperNikRequestBody: RequestBody =
                AppHelpers.makeRequestBody(nik)
            val keeperNameRequestBody: RequestBody = AppHelpers.makeRequestBody(keeperName)
            val keeperAddressRequestBody: RequestBody = AppHelpers.makeRequestBody(keeperAddress)

            val imageFile = FileHelper.uriToFile(
                this@TokoStoreViewModel._selectedToko.value ?: Uri.parse(""), context
            ).reduceFileImage()

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val photoMultipartBody = MultipartBody.Part.createFormData(
                "store_photo", imageFile.name, requestImageFile
            )

            val addressRequestBody: RequestBody =
                AppHelpers.makeRequestBody(this@TokoStoreViewModel._selectedMap.value?.displayName)
            val latRequestBody: RequestBody =
                AppHelpers.makeRequestBody(this@TokoStoreViewModel._selectedMap.value?.lat)
            val longRequestBody: RequestBody =
                AppHelpers.makeRequestBody(this@TokoStoreViewModel._selectedMap.value?.lon)

            val response = async {
                tokoRepository.store(
                    token = token,
                    name = nameRequestBody,
                    ownerName = ownerNameRequestBody,
                    phone = phoneRequestBody,
                    keeperNik = keeperNikRequestBody,
                    keeperName = keeperNameRequestBody,
                    keeperAddress = keeperAddressRequestBody,
                    lat = latRequestBody,
                    long = longRequestBody,
                    address = addressRequestBody,
                    ktp = ktpRequestBody,
                    storePhoto = photoMultipartBody,
                )
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _storeTokoState.postValue(
                    AppState.Error(
                        message = errorResponse.message
                            ?: "Ada kesalahan, silahkan coba lagi beberapa saat lagi.",
                    )
                )
                return@launch
            }

            _storeTokoState.postValue(
                AppState.Success(
                    message = "Berhasil menyimpan toko.",
                    data = BaseResponse(),
                )
            )
        }
    }

    fun doOcr(context: Context, capturedImage: Uri) {
        val coroutineExceptionHandler = CoroutineExceptionHandler { err, message ->
            AppHelpers.log(err.toString())

            AppHelpers.log(message.toString())
            AppHelpers.log(message.stackTrace.toString())

            _ocrState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {
            _ocrState.postValue(AppState.Loading)

            val imageFile = FileHelper.uriToFile(capturedImage, context).reduceFileImage()

            AppHelpers.log(AppHelpers.getFileSizeInMB(imageFile))

            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
            val photoMultipartBody = MultipartBody.Part.createFormData(
                "ktp_photo", imageFile.name, requestImageFile
            )


            val response = async {
                tokoRepository.ocr(
                    token,
                    photo = photoMultipartBody,
                )
            }.await()

            if (!response.isSuccessful) {
                val errorResponse: ErrorResponse = Gson().fromJson(
                    response.errorBody()!!.charStream(), ErrorResponse::class.java
                )
                _ocrState.postValue(
                    AppState.Error(
                        message = errorResponse.message ?: "",
                    )
                )
                return@launch
            }

            val dataOcr: DataOcr = response.body()?.data ?: DataOcr()

            _ocrState.postValue(
                AppState.Success(
                    message = "Berhasil",
                    data = dataOcr,
                )
            )
        }
    }
}