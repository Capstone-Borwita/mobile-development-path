package com.untillness.borwita.ui.toko_store

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.responses.GeoreverseResponse
import com.untillness.borwita.databinding.ActivityTokoStoreBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.ui.capture.CaptureActivity
import com.untillness.borwita.ui.map.MapsActivity

class TokoStoreActivity : Unfocus(), OnMapReadyCallback {
    private lateinit var binding: ActivityTokoStoreBinding
    private lateinit var map: GoogleMap

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

        title = "Tambah Toko"


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        this.triggers()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun triggers(){
        this.binding.apply {
            buttonKameraKtp.setOnClickListener {
                val intent = Intent(this@TokoStoreActivity, CaptureActivity::class.java)
                startActivity(intent)
            }

            buttonMapToko.setOnClickListener {
                val intent = Intent(this@TokoStoreActivity, MapsActivity::class.java)
                resultLauncher.launch(intent)
            }
        }
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val resultMap = if (Build.VERSION.SDK_INT >= 33) {
                result.data?.getParcelableExtra<GeoreverseResponse>(MapsActivity.RESULT, GeoreverseResponse::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra<GeoreverseResponse>(MapsActivity.RESULT)
            }

            AppHelpers.log(resultMap.toString())

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}