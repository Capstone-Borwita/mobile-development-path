package com.untillness.borwita.ui.capture

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.untillness.borwita.MainActivity
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.responses.Data
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.remote.responses.OcrResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityCaptureBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ExifHelper
import com.untillness.borwita.helpers.FileHelper
import com.untillness.borwita.helpers.ImageClassifierHelper
import com.untillness.borwita.helpers.ImageHelper
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.widgets.AppDialog
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaptureBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var viewFinderRect: Rect

    var animator: ObjectAnimator? = null
    private lateinit var outputDirectory: File

    private lateinit var captureViewModel: CaptureViewModel

    private lateinit var cameraProvider: ProcessCameraProvider

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var imageAnalyzer: ImageAnalysis

    // Preview
    private lateinit var preview: Preview

    // Select back camera as a default
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var appDialog: AppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.captureViewModel = ViewModelFactory.obtainViewModel<CaptureViewModel>(this)
        appDialog = AppDialog(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Ambil Foto KTP"

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = FileHelper.getOutputDirectory(this)

        preview = Preview.Builder().build().also {
            it.surfaceProvider = binding.viewFinder.surfaceProvider
        }

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        this.triggers()

        this.listeners()

        this.cropRectanglePreview()

        this.animateScanEffect()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun animateScanEffect() {
        val vto: ViewTreeObserver = binding.viewFinderWindow.viewTreeObserver

        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.viewFinderWindow.viewTreeObserver.removeOnGlobalLayoutListener(this)

                animator = ObjectAnimator.ofFloat(
                    binding.scannerBar,
                    "translationY",
                    binding.viewFinderWindow.y,
                    (binding.viewFinderWindow.y + binding.viewFinderWindow.height)
                )
                animator?.repeatMode = ValueAnimator.REVERSE
                animator?.repeatCount = ValueAnimator.INFINITE
                animator?.interpolator = AccelerateDecelerateInterpolator()
                animator?.setDuration(3000)
                animator?.start()
            }
        })
    }

    private fun listeners() {
        this.captureViewModel.apply {
            captureState.observe(this@CaptureActivity) {
                when (it) {
                    AppState.Standby -> {
                        this@CaptureActivity.binding.iconCamera.isVisible = true
                        this@CaptureActivity.binding.loading.isVisible = false
                    }

                    AppState.Loading -> {
                        this@CaptureActivity.binding.iconCamera.isVisible = false
                        this@CaptureActivity.binding.loading.isVisible = true
                    }

                    is AppState.Error -> {
                        this@CaptureActivity.stopCamera()

                        this@CaptureActivity.binding.iconCamera.isVisible = true
                        this@CaptureActivity.binding.loading.isVisible = false

                        AppDialog.error(
                            context = this@CaptureActivity,
                            message = this@CaptureActivity.getString(R.string.ada_kesalahan_silahkan_coba_lagi_beberapa_saat_lagi)
                        )
                    }

                    is AppState.Success -> {
                        this@CaptureActivity.stopCamera()

                        this@CaptureActivity.binding.iconCamera.isVisible = true
                        this@CaptureActivity.binding.loading.isVisible = false

                        val dialog = BottomSheetDialog(this@CaptureActivity)

                        // on below line we are inflating a layout file which we have created.
                        val view = layoutInflater.inflate(R.layout.bottom_sheet_capture, null)

                        val imageResult = view.findViewById<ImageView>(R.id.image_result)
                        Glide.with(this@CaptureActivity).load(it.data.path)
                            .placeholder(AppHelpers.circularProgressDrawable(this@CaptureActivity))
                            .centerCrop().into(imageResult)

                        // on below line we are creating a variable for our button
                        // which we are using to dismiss our dialog.
                        val btnClose = view.findViewById<Button>(R.id.button_cancel)
                        val btnSubmit = view.findViewById<Button>(R.id.button_submit)

                        // on below line we are adding on click listener
                        // for our dismissing the dialog button.
                        btnClose.setOnClickListener {
                            // on below line we are calling a dismiss
                            // method to close our dialog.
                            dialog.dismiss()
                            this@CaptureActivity.resumeCamera()
                        }
                        btnSubmit.setOnClickListener {
                            this@CaptureActivity.captureViewModel.doOcr(this@CaptureActivity)
                        }
                        // below line is use to set cancelable to avoid
                        // closing of dialog box when clicking on the screen.
                        dialog.setCancelable(false)

                        // on below line we are setting
                        // content view to our view.
                        dialog.setContentView(view)

                        // on below line we are calling
                        // a show method to display a dialog.
                        dialog.show()
                    }
                }
            }

            ocrState.observe(this@CaptureActivity) {
                when (it) {
                    AppState.Loading -> {
                        appDialog.showLoadingDialog()
                    }

                    AppState.Standby -> {
                        appDialog.hideLoadingDialog()
                    }

                    is AppState.Error -> {
                        appDialog.hideLoadingDialog()
                        AppDialog.error(
                            context = this@CaptureActivity, message = it.message
                        )
                    }

                    is AppState.Success -> {
                        val dataOcr: DataOcr = it.data
                        dataOcr.localPath = this@CaptureActivity.captureViewModel.capturedImage.path

                        val resultIntent = Intent()
                        resultIntent.putExtra(
                            RESULT_OCR_EXTRA, it.data,
                        )
                        setResult(RESULT_OCR_CODE, resultIntent)
                        finish()
                    }
                }
            }
        }
    }

    private fun triggers() {
        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun cropRectanglePreview() {
        binding.viewFinder.post {
            viewFinderRect = Rect(
                binding.viewFinderWindow.left,
                binding.viewFinderWindow.top,
                binding.viewFinderWindow.right,
                binding.viewFinderWindow.bottom
            )
            binding.viewFinderBackground.setViewFinderRect(viewFinderRect)
        }
    }

    private fun takePhoto() {
        if (this@CaptureActivity.captureViewModel.captureState.value is AppState.Loading) return
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        this@CaptureActivity.captureViewModel.assignCaptureState(
            AppState.Loading
        )

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri!!

                    this@CaptureActivity.captureViewModel.captureAndCrop(
                        context = this@CaptureActivity,
                        contentResolver = this@CaptureActivity.contentResolver,
                        uri = uri,
                        outputDirectory = outputDirectory,
                        photoExtension = PHOTO_EXTENSION,
                        fileNameFormat = FILENAME_FORMAT,
                        viewFinder = binding.viewFinder,
                        viewFinderRect = viewFinderRect,
                    )
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        imageClassifierHelper = ImageClassifierHelper(context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        AppHelpers.log(error)
                    }
                }

                override fun onResults(results: List<Classifications>?) {
                    runOnUiThread {
                        AppHelpers.log(results.toString())
                    }
                }
            })

        cameraProviderFuture.addListener({
            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder().setResolutionSelector(resolutionSelector)
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build()
            imageAnalyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
                imageClassifierHelper.classifyImage(image)
            }

            // Used to bind the lifecycle of cameras to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            try {
                this.stopCamera()

                this.resumeCamera()

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle Permission granted/rejected
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
        }
        if (!permissionGranted) {
            finish()
            Toast.makeText(
                baseContext, "Permission request denied", Toast.LENGTH_SHORT
            ).show()
        } else {
            startCamera()
        }
    }

    private fun stopCamera() {
        // Unbind use cases before rebinding
        cameraProvider.unbindAll()

        animator?.pause()
    }

    private fun resumeCamera() {

        // Bind use cases to camera
        cameraProvider.bindToLifecycle(
            this,
            cameraSelector,
            preview,
            imageCapture,
            imageAnalyzer,
        )

        animator?.start()
    }

    companion object {
        private const val TAG = "Borwita"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        ).apply {
//            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()


        const val RESULT_OCR_EXTRA = "RESULT_OCR_EXTRA"
        const val RESULT_OCR_CODE = 222
    }
}