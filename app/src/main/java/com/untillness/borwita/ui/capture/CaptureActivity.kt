package com.untillness.borwita.ui.capture

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.ContentValues
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.untillness.borwita.MainActivity
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityCaptureBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ExifHelper
import com.untillness.borwita.helpers.FileHelper
import com.untillness.borwita.helpers.ImageHelper
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Ambil Foto KTP"

        cameraExecutor = Executors.newSingleThreadExecutor()
        outputDirectory = FileHelper.getOutputDirectory(this)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        this.triggers()

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

    private fun triggers() {
        // Set up the listeners for take photo and video capture buttons
        binding.imageCaptureButton.setOnClickListener { takePhoto() }
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
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

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
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val uri = output.savedUri!!


                    //get saved file as bitmap
                    val bitmap: Bitmap = ImageDecoder.decodeBitmap(
                        ImageDecoder.createSource(
                            this@CaptureActivity.contentResolver, uri
                        )
                    )

                    val photoFile = ImageHelper.createFile(outputDirectory, FILENAME_FORMAT, PHOTO_EXTENSION)
                    val cropped = ImageHelper.cropImage(
                        ExifHelper.rotateBitmap(uri.path!!, bitmap),
                        Size(binding.viewFinder.width, binding.viewFinder.height),
                        viewFinderRect
                    )
                    val newUri = ImageHelper.storeImage(cropped, photoFile)

                    AppHelpers.log(newUri.toString())

                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

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

    companion object {
        private const val TAG = "Borwita"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        ).apply {
//            add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }.toTypedArray()
    }
}