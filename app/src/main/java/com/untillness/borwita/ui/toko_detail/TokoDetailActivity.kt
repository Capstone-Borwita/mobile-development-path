package com.untillness.borwita.ui.toko_detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityTokoDetailBinding
import com.untillness.borwita.helpers.ViewModelFactory

class TokoDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTokoDetailBinding
    private lateinit var tokoDetailViewModel: TokoDetailViewModel

    private var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityTokoDetailBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.tokoDetailViewModel = ViewModelFactory.obtainViewModel<TokoDetailViewModel>(this)

        this.id = intent.getStringExtra(EXTRA_ID) ?: ""
        this.tokoDetailViewModel.id = this.id

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

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
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