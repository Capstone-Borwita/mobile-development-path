package com.untillness.borwita.ui.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.api.Api
import com.untillness.borwita.data.states.ApiState
import com.untillness.borwita.databinding.ActivityMapsBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        this.mapViewModel = ViewModelFactory.obtainViewModel<MapViewModel>(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.listeners()
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

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                this@MapsActivity.mapViewModel.currentLatLong.value ?: LatLng(
                    0.toDouble(), 0.toDouble()
                ), 18f
            )
        )

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

    private fun listeners() {
        this.mapViewModel.apply {
            mapApiState.observe(this@MapsActivity) {
                when (it) {
                    is ApiState.Error, ApiState.Standby -> {
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

                    ApiState.Loading -> {
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

                    is ApiState.Success -> {
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
}