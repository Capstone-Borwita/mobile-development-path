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
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ExifHelper
import com.untillness.borwita.helpers.ImageHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class CaptureViewModel : ViewModel() {
    private val _captureState: MutableLiveData<AppState<Uri>> = MutableLiveData(AppState.Standby)
    val captureState: LiveData<AppState<Uri>> = _captureState

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

            AppHelpers.log(newUri.toString())

            this@CaptureViewModel._captureState.postValue(
                AppState.Success<Uri>(
                    message = "Berhasil",
                    data = newUri,
                )
            )
        }
    }
}