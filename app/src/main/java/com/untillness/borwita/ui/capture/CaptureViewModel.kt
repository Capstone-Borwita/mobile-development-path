package com.untillness.borwita.ui.capture

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.util.Size
import androidx.camera.view.PreviewView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.repositories.SharePrefRepository
import com.untillness.borwita.data.remote.repositories.TokoRepository
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.ErrorResponse
import com.untillness.borwita.data.remote.responses.OcrResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ExifHelper
import com.untillness.borwita.helpers.FileHelper
import com.untillness.borwita.helpers.ImageHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CaptureViewModel(context: Context) : ViewModel() {
    private var sharePrefRepository: SharePrefRepository = SharePrefRepository(
        context = context,
    )
    private var token: String = this.sharePrefRepository.getToken()

    private var tokoRepository: TokoRepository = TokoRepository()

    private val _captureState: MutableLiveData<AppState<Uri>> = MutableLiveData(AppState.Standby)
    val captureState: LiveData<AppState<Uri>> = _captureState
    lateinit var capturedImage: Uri

    private val _ocrState: MutableLiveData<AppState<DataOcr>> = MutableLiveData(AppState.Standby)
    val ocrState: LiveData<AppState<DataOcr>> = _ocrState

    fun assignCaptureState(newVal: AppState<Uri>) {
        this._captureState.value = newVal
    }

    fun captureAndCrop(
        context: Context, contentResolver: ContentResolver, uri: Uri, outputDirectory: File,
        fileNameFormat: String,
        photoExtension: String,
        viewFinder: PreviewView,
        viewFinderRect: Rect,
    ) {

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
            _captureState.postValue(
                AppState.Error(
                    message = context.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                )
            )
        }

        CoroutineScope(coroutineExceptionHandler).launch {

            //get saved file as bitmap
            val bitmap: Bitmap = ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(
                    contentResolver, uri
                )
            )

            val photoFile = ImageHelper.createFile(outputDirectory, fileNameFormat, photoExtension)

            val cropped = ImageHelper.cropImage(
                ExifHelper.rotateBitmap(uri.path!!, bitmap),
                Size(viewFinder.width, viewFinder.height),
                viewFinderRect
            )
            val newUri = ImageHelper.storeImage(cropped, photoFile)

            capturedImage = newUri
            this@CaptureViewModel._captureState.postValue(
                AppState.Success<Uri>(
                    message = "Berhasil",
                    data = newUri,
                )
            )
        }
    }


    fun doOcr(context: Context) {
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

            val imageFile = FileHelper.uriToFile(capturedImage, context)

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