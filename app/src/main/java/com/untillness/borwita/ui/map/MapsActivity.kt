package com.untillness.borwita.ui.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityMapsBinding
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.widgets.AppDialog

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapViewModel: MapViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.mapViewModel = ViewModelFactory.obtainViewModel<MapViewModel>(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.listeners()

        this.triggers()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap


        this.moveCameraMap()

        val oldPosition = map.cameraPosition.target

        map.setOnCameraMoveStartedListener {
            // drag started
            this@MapsActivity.mapViewModel.setLoadingState()

            // start animation
            this.binding.pin.animate().translationY(-50f).setStartDelay(0).withEndAction {}.start()
            this.binding.pinShadow.animate().withStartAction {
                this.binding.pinShadow.setPadding(5, 0, 5, 0)
            }.start()
        }

        map.setOnCameraIdleListener {
            val newPosition = map.cameraPosition.target
            this@MapsActivity.mapViewModel.setCurrentLatLong(newPosition)

            if (newPosition != oldPosition) {

                // start animation
                this.binding.pin.animate().translationY(0f).setStartDelay(2000).withEndAction {
                    // drag ended
                    this@MapsActivity.mapViewModel.getGeoreverse(this@MapsActivity.applicationContext)
                }.start()

                this.binding.pinShadow.animate().withStartAction {
                    this.binding.pinShadow.setPadding(0, 0, 0, 0)
                }.start()
            }
        }
    }

    private fun moveCameraMap(newPosition: LatLng? = null) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                newPosition ?: this@MapsActivity.mapViewModel.currentLatLong.value ?: LatLng(
                    0.toDouble(), 0.toDouble()
                ), 18f
            )
        )
    }

    private fun listeners() {
        this.mapViewModel.apply {
            mapAppState.observe(this@MapsActivity) {
                when (it) {
                    is AppState.Error, AppState.Standby -> {
                        this@MapsActivity.binding.apply {
                            bottomSheetMap.apply {
                                textPlaceholder.isVisible = true
                                loading.isVisible = false
                                textTitle.isVisible = false
                                textAddress.isVisible = false
                                buttonSubmit.isEnabled = false
                            }
                        }
                    }

                    AppState.Loading -> {
                        this@MapsActivity.binding.apply {
                            bottomSheetMap.apply {
                                textPlaceholder.isVisible = false
                                loading.isVisible = true
                                textTitle.isVisible = false
                                textAddress.isVisible = false
                                buttonSubmit.isEnabled = false
                            }
                        }
                    }

                    is AppState.Success -> {
                        this@MapsActivity.binding.apply {
                            bottomSheetMap.apply {
                                textPlaceholder.isVisible = false
                                loading.isVisible = false
                                textTitle.isVisible = true
                                textAddress.isVisible = true
                                buttonSubmit.isEnabled = true

                                textAddress.text = it.data.displayName ?: "-"
                            }
                        }
                    }
                }
            }

        }
    }

    private fun triggers() {
        this.binding.apply {
            bottomSheetMap.buttonGps.setOnClickListener {
                this@MapsActivity.getMyLastLocation()
            }
            bottomSheetMap.buttonSubmit.setOnClickListener {
                val resultIntent = Intent()
                resultIntent.putExtra(
                    RESULT_MAP_EXTRA, GeoreverseResponse(
                        displayName = this@MapsActivity.mapViewModel.displayMap,
                        lat = this@MapsActivity.mapViewModel.currentLatLong.value?.latitude.toString(),
                        lon = this@MapsActivity.mapViewModel.currentLatLong.value?.longitude.toString(),
                    )
                )
                setResult(RESULT_MAP_CODE, resultIntent)
                finish()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val newPosition = LatLng(
                        location.latitude, location.longitude
                    )
                    this@MapsActivity.mapViewModel.setCurrentLatLong(
                        newPosition
                    )
                    this@MapsActivity.moveCameraMap(newPosition)
                } else {
                    AppDialog.error(
                        context = this@MapsActivity,
                        message = "Lokasi tidak ditemukan, silahkan coba lagi.",
                    )
                }
            }
        } else {
            requestPermissionLocationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLocationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getMyLastLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getMyLastLocation()
            }

            else -> {
                AppDialog.error(
                    context = this@MapsActivity,
                    message = getString(R.string.izin_ke_lokasi_ditolak_fitur_tidak_bisa_digunakan),
                )
            }
        }
    }


    companion object {
        const val RESULT_MAP_EXTRA = "RESULT_MAP_EXTRA"
        const val RESULT_MAP_CODE = 111
    }
}