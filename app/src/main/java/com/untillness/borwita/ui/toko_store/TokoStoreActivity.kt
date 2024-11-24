package com.untillness.borwita.ui.toko_store

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.databinding.ActivityTokoStoreBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.capture.CaptureActivity
import com.untillness.borwita.ui.map.MapsActivity
import java.io.File

class TokoStoreActivity : Unfocus(), OnMapReadyCallback {
    private lateinit var binding: ActivityTokoStoreBinding
    private lateinit var map: GoogleMap
    private lateinit var tokoStoreViewModel: TokoStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityTokoStoreBinding.inflate(layoutInflater)

        setContentView(this.binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        this.tokoStoreViewModel = ViewModelFactory.obtainViewModel<TokoStoreViewModel>(this)

        title = "Tambah Toko"


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.triggers()

        this.listeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun triggers() {
        this.binding.apply {
            buttonKameraKtp.setOnClickListener {
                val intent = Intent(this@TokoStoreActivity, CaptureActivity::class.java)
                resultLauncher.launch(intent)
            }


            buttonGaleriToko.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonMapToko.setOnClickListener {
                val intent = Intent(this@TokoStoreActivity, MapsActivity::class.java)
                resultLauncher.launch(intent)
            }
        }
    }

    private fun listeners() {
        this.tokoStoreViewModel.apply {
            selectedKtp.observe(this@TokoStoreActivity) {
                this@TokoStoreActivity.binding.apply {
                    Glide.with(this@TokoStoreActivity).load(it.localPath?.let { File(it).path })
                        .placeholder(AppHelpers.circularProgressDrawable(this@TokoStoreActivity))
                        .centerCrop().into(imageKtp)

                    textPlaceholderImageKtp.isVisible = false
                    imageKtp.isVisible = true
                }
            }
            selectedToko.observe(this@TokoStoreActivity) {
                this@TokoStoreActivity.binding.apply {
                    Glide.with(this@TokoStoreActivity).load(it)
                        .placeholder(AppHelpers.circularProgressDrawable(this@TokoStoreActivity))
                        .centerCrop().into(imageToko)

                    textPlaceholderImageToko.isVisible = false
                    imageToko.isVisible = true
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == MapsActivity.RESULT_MAP_CODE && result.data != null) {
            AppHelpers.log(result.data.toString())

            val data = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra<GeoreverseResponse>(
                    MapsActivity.RESULT_MAP_EXTRA, GeoreverseResponse::class.java
                )
            } else {
                @Suppress("DEPRECATION") result.data?.getParcelableExtra<GeoreverseResponse>(
                    MapsActivity.RESULT_MAP_EXTRA
                )
            }
        }

        // | ================================================================================================================
        // | FROM
        // | Capture Activity
        if (result.resultCode == CaptureActivity.RESULT_OCR_CODE && result.data != null) {
            val data = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra<DataOcr>(
                    CaptureActivity.RESULT_OCR_EXTRA, DataOcr::class.java
                )
            } else {
                @Suppress("DEPRECATION") result.data?.getParcelableExtra<DataOcr>(CaptureActivity.RESULT_OCR_EXTRA)
            }

            if (data != null) {
                this.tokoStoreViewModel.assignSelectedKtp(data)
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult

        this.tokoStoreViewModel.assignSelectedToko(uri)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setAllGesturesEnabled(false)
    }
}