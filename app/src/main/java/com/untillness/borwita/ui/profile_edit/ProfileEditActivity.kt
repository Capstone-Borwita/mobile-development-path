package com.untillness.borwita.ui.profile_edit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.untillness.borwita.R
import com.untillness.borwita.data.states.ApiState
import com.untillness.borwita.databinding.ActivityProfileEditBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.wrapper.WrapperViewModel

class ProfileEditActivity : Unfocus() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var wrapperViewModel: WrapperViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.wrapperViewModel = ViewModelFactory.obtainViewModel<WrapperViewModel>(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Ubah Profil"

        this.initState()

        this.triggers()

        this.listeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun initState() {
        this.wrapperViewModel.initState(this)
    }

    private fun triggers() {
        this.binding.apply {
            main.setOnRefreshListener {
                this@ProfileEditActivity.wrapperViewModel.initState(this@ProfileEditActivity)
                main.isRefreshing = false
            }
        }
    }

    private fun listeners() {
        this.wrapperViewModel.apply {
            profileState.observe(this@ProfileEditActivity) {

                when (it) {
                    ApiState.Loading -> {
                        this@ProfileEditActivity.binding.apply {
                            mainSection.isVisible = false
                            loading.isVisible = true
                        }
                    }

                    ApiState.Standby -> {
                        this@ProfileEditActivity.binding.apply {
                            mainSection.isVisible = false
                            loading.isVisible = false
                        }
                    }

                    is ApiState.Error -> {
                        this@ProfileEditActivity.binding.apply {
                            mainSection.isVisible = false
                            loading.isVisible = false
                        }
                    }

                    is ApiState.Success -> {
                        this@ProfileEditActivity.binding.apply {
                            mainSection.isVisible = true
                            loading.isVisible = false

                            fieldName.editText?.setText(it.data.data?.name ?: "-")

                            fieldEmail.isEnabled = false
                            fieldEmail.editText?.setText(it.data.data?.email ?: "-")

                            Glide.with(this@ProfileEditActivity)
                                .load(it.data.data?.avatarPath ?: "-")
                                .placeholder(AppHelpers.circularProgressDrawable(this@ProfileEditActivity))
                                .fitCenter().into(imageAvatar)
                        }
                    }
                }
            }
        }
    }
}