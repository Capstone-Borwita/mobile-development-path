package com.untillness.borwita.ui.toko_store

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityTokoStoreBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.ui.capture.CaptureActivity

class TokoStoreActivity : Unfocus() {
    private lateinit var binding: ActivityTokoStoreBinding

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
        }
    }
}