package com.untillness.borwita.ui.toko_detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityTokoDetailBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.ViewModelFactory

class TokoDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityTokoDetailBinding
    private lateinit var tokoDetailViewModel: TokoDetailViewModel
    private lateinit var map: GoogleMap

    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityTokoDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.tokoDetailViewModel = ViewModelFactory.obtainViewModel<TokoDetailViewModel>(this)

        this.id = intent.getStringExtra(EXTRA_ID) ?: ""
        this.tokoDetailViewModel.id = this.id

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        ViewCompat.setOnApplyWindowInsetsListener(this.binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Detail Toko"

        this.tokoDetailViewModel.loadData(this)

        this.listeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_toko_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == com.untillness.borwita.R.id.button_delete_toko) {
            confirmDelete(context = this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setAllGesturesEnabled(false)
    }

    private fun listeners() {
        this.tokoDetailViewModel.apply {
            detailTokoState.observe(this@TokoDetailActivity) {
                when (it) {
                    AppState.Standby, AppState.Loading -> {
                        this@TokoDetailActivity.binding.apply {
                            sectionLoading.isVisible = true
                            sectionContent.isVisible = false
                            emptyData.emptyData.isVisible = false
                        }
                    }

                    is AppState.Error -> {

                        this@TokoDetailActivity.binding.apply {
                            sectionLoading.isVisible = false
                            sectionContent.isVisible = false
                            emptyData.emptyData.isVisible = true

                            emptyData.emptyDataText.text = it.message
                        }
                    }

                    is AppState.Success -> {

                        this@TokoDetailActivity.binding.apply {
                            sectionLoading.isVisible = false
                            sectionContent.isVisible = true
                            emptyData.emptyData.isVisible = false

                            textValueName.text = it.data.name
                            textValueNameOwner.text = it.data.ownerName
                            textValueNomorTeleponOwner.text = it.data.keeperPhoneNumber
                            Glide.with(this@TokoDetailActivity).load(it.data.ktpPhotoPath)
                                .placeholder(AppHelpers.circularProgressDrawable(this@TokoDetailActivity))
                                .centerCrop().into(imageKtp)

                            textValueNik.text = it.data.keeperNik
                            textValueNameKtp.text = it.data.keeperName
                            textValueAlamatKtp.text = it.data.keeperAddress

                            Glide.with(this@TokoDetailActivity).load(it.data.storePhotoPath)
                                .placeholder(AppHelpers.circularProgressDrawable(this@TokoDetailActivity))
                                .centerCrop().into(imageToko)

                            textValueAlamat.text = it.data.georeverse


                            val newPosition: LatLng = LatLng(
                                it.data.latitude?.toDouble() ?: 0.toDouble(),
                                it.data.longitude?.toDouble() ?: 0.toDouble()
                            )

                            map.clear()
                            map.addMarker(
                                MarkerOptions().position(newPosition)
                            )
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    newPosition, 14f
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ID = "EXTRA_ID"
    }
}