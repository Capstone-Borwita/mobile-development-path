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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.responses.DataOcr
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityTokoStoreBinding
import com.untillness.borwita.extensions.isEmpty
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.capture.CaptureActivity
import com.untillness.borwita.ui.map.MapsActivity
import com.untillness.borwita.ui.map.MapsActivity.Companion.RESULT_MAP_CODE
import com.untillness.borwita.ui.map.MapsActivity.Companion.RESULT_MAP_EXTRA
import com.untillness.borwita.widgets.AppDialog
import java.io.File

class TokoStoreActivity : Unfocus(), OnMapReadyCallback {
    private lateinit var binding: ActivityTokoStoreBinding
    private lateinit var map: GoogleMap
    private lateinit var tokoStoreViewModel: TokoStoreViewModel
    private lateinit var appDialog: AppDialog

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
        this.appDialog = AppDialog(this)

        title = "Tambah Toko"

        this.binding.sectionMap.isVisible = false
        this.binding.textPlaceholderAlamat.isVisible = false

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
            buttonGaleriKtp.setOnClickListener {
                launchGalleryKtp.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonMapToko.setOnClickListener {
                val intent = Intent(this@TokoStoreActivity, MapsActivity::class.java)
                resultLauncher.launch(intent)
            }
            buttonStore.setOnClickListener {
                doStoreToko()
            }
        }
    }

    private fun listeners() {
        this.tokoStoreViewModel.apply {
            selectedKtp.observe(this@TokoStoreActivity) {
                this@TokoStoreActivity.binding.apply {
                    Glide.with(this@TokoStoreActivity).load(it.localPath?.let { File(it).path })
                        .placeholder(AppHelpers.circularProgressDrawable(this@TokoStoreActivity))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerCrop().into(imageKtp)

                    textPlaceholderImageKtp.isVisible = false
                    imageKtp.isVisible = true

                    fieldNomorNik.editText?.setText(it.nik)
                    fieldNameKtp.editText?.setText(it.name)
                    fieldAlamatKtp.editText?.setText(it.address)
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
            selectedMap.observe(this@TokoStoreActivity) {
                this@TokoStoreActivity.binding.apply {
                    sectionMap.isVisible = true
                    textPlaceholderAlamat.isVisible = true
                    sectionAlamat.isVisible = false

                    textPlaceholderAlamat.text = it.displayName ?: "-"
                }

                val newPosition: LatLng =
                    LatLng(it.lat?.toDouble() ?: 0.toDouble(), it.lon?.toDouble() ?: 0.toDouble())

                map.clear()
                map.addMarker(
                    MarkerOptions().position(newPosition).title(it.displayName)
                )
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        newPosition, 14f
                    )
                )
            }

            storeTokoState.observe(this@TokoStoreActivity) {
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
                            this@TokoStoreActivity, message = it.message
                        )
                    }

                    is AppState.Success<*> -> {
                        appDialog.hideLoadingDialog()
                        AppDialog.success(
                            this@TokoStoreActivity,
                            message = "Berhasil menambah data toko.",
                            callback = object : AppDialog.Companion.AppDialogCallback {

                                override fun onDismiss() {
                                    val resultIntent = Intent()
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                }

                                override fun onConfirm() {
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // | ================================================================================================================
        // | FROM
        // | Map Activity
        if (result.resultCode == MapsActivity.RESULT_MAP_CODE && result.data != null) {
            val data = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra<GeoreverseResponse>(
                    MapsActivity.RESULT_MAP_EXTRA, GeoreverseResponse::class.java
                )
            } else {
                @Suppress("DEPRECATION") result.data?.getParcelableExtra<GeoreverseResponse>(
                    MapsActivity.RESULT_MAP_EXTRA
                )
            }

            if (data != null) {
                this.tokoStoreViewModel.assignSelectedMap(data)
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

    private val launchGalleryKtp = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult

        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))

        val options = UCrop.Options()
        options.setAspectRatioOptions(
            0, AspectRatio(
                "KTP",
                19.toFloat(),
                12.toFloat(),
            )
        )

        UCrop.of(uri, destinationUri).withOptions(options).start(this)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult

        this.tokoStoreViewModel.assignSelectedToko(uri)
    }

    private fun doStoreToko() {
        binding.fieldStore.error =
            if (binding.fieldStore.isEmpty()) "Nama Toko wajib diisi" else null

        binding.fieldName.error =
            if (binding.fieldName.isEmpty()) "Nama Pemilik Toko wajib diisi" else null

        binding.fieldPhone.error =
            if (binding.fieldPhone.isEmpty()) "Nomor Telepon wajib diisi" else null

        binding.fieldNomorNik.error =
            if (binding.fieldNomorNik.isEmpty()) "Nomor NIK wajib diisi" else null

        binding.fieldNameKtp.error =
            if (binding.fieldNameKtp.isEmpty()) "Nama Sesuai KTP wajib diisi" else null

        binding.fieldAlamatKtp.error =
            if (binding.fieldAlamatKtp.isEmpty()) "Alamat Sesuai KTP wajib diisi" else null

        if (hasErrorFields()) {
            Snackbar.make(
                this.binding.root,
                getString(R.string.silahkan_mengisi_field_diatas_terlebih_dahulu),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        if (this.tokoStoreViewModel.selectedToko.value == null) {
            Snackbar.make(
                this.binding.root, "Foto Toko wajib diisi.", Snackbar.LENGTH_SHORT
            ).show()
            return
        }
        if (this.tokoStoreViewModel.selectedMap.value == null) {
            Snackbar.make(
                this.binding.root, "Alamat Toko wajib diisi.", Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        this@TokoStoreActivity.tokoStoreViewModel.store(
            context = this,
            name = binding.fieldStore.editText?.text.toString(),
            ownerName = binding.fieldName.editText?.text.toString(),
            phone = binding.fieldPhone.editText?.text.toString(),
            keeperName = binding.fieldNameKtp.editText?.text.toString(),
            keeperAddress = binding.fieldAlamatKtp.editText?.text.toString(),
            nik = binding.fieldNomorNik.editText?.text.toString(),
        )
    }

    private fun hasErrorFields(): Boolean {
        val errors: List<CharSequence> = mutableListOf(
            binding.fieldStore.error,
            binding.fieldName.error,
            binding.fieldPhone.error,
            binding.fieldNomorNik.error,
            binding.fieldNameKtp.error,
            binding.fieldAlamatKtp.error,
        ).filterNotNull()

        return errors.isNotEmpty()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setAllGesturesEnabled(false)
    }
}