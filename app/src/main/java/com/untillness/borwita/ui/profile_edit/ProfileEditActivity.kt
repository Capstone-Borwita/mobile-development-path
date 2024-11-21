package com.untillness.borwita.ui.profile_edit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.requests.ProfileEditRequest
import com.untillness.borwita.data.states.ApiState
import com.untillness.borwita.databinding.ActivityProfileEditBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.wrapper.WrapperViewModel
import com.untillness.borwita.widgets.AppDialog
import java.io.File
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileEditActivity : Unfocus() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var wrapperViewModel: WrapperViewModel
    private lateinit var profileEditViewModel: ProfileEditViewModel
    private lateinit var appDialog: AppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.wrapperViewModel = ViewModelFactory.obtainViewModel<WrapperViewModel>(this)
        this.profileEditViewModel = ViewModelFactory.obtainViewModel<ProfileEditViewModel>(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        this.appDialog = AppDialog(this)

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
    }

    private fun triggers() {
        this.binding.apply {
            main.setOnRefreshListener {
                this@ProfileEditActivity.wrapperViewModel.initState(this@ProfileEditActivity)
                main.isRefreshing = false
            }
            buttonSelectImage.setOnClickListener {
                launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            buttonSaveProfile.setOnClickListener {
                doRegister()
            }
        }
    }

    private fun listeners() {
        binding.apply {
            fieldName.editText?.doOnTextChanged { value, _, _, _ ->
                val length: Int = value?.length ?: 0

                if (length == 0) {
                    fieldName.error = getString(R.string.nama_wajib_diisi)
                    return@doOnTextChanged
                }

                if (length < 3) {
                    fieldName.error = getString(R.string.panjang_nama_minimal_3_karakter)
                    return@doOnTextChanged
                } else {
                    fieldName.isErrorEnabled = false
                    fieldName.error = null
                    return@doOnTextChanged
                }
            }
        }
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

        this.profileEditViewModel.profileEditPhotoState.observe(this) {

            when (it) {
                ApiState.Loading -> {
                    appDialog.showLoadingDialog()

                }

                ApiState.Standby -> {
                    appDialog.hideLoadingDialog()

                }

                is ApiState.Error -> {
                    appDialog.hideLoadingDialog()
                    AppDialog.error(
                        this@ProfileEditActivity, message = it.message
                    )
                }

                is ApiState.Success -> {
                    appDialog.hideLoadingDialog()
                    this@ProfileEditActivity.wrapperViewModel.initState(this@ProfileEditActivity)

                    AppDialog.success(this@ProfileEditActivity, message = it.message)

                }
            }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))

        val options = UCrop.Options()
        options.setAspectRatioOptions(
            0, AspectRatio(
                "Kotak",
                1.toFloat(),
                1.toFloat(),
            )
        )

        UCrop.of(uri, destinationUri).withOptions(options).start(this)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)

            this@ProfileEditActivity.profileEditViewModel.profileEditPhoto(
                context = this@ProfileEditActivity,
                photo = resultUri ?: Uri.parse(""),
            )
        }
    }

    private fun hasEmptyFields(): Boolean {
        val fieldText: List<Int> = mutableListOf(
            binding.fieldName.editText?.text?.length ?: 0,
            binding.fieldEmail.editText?.text?.length ?: 0,
        )

        return fieldText.contains(0)
    }

    private fun hasErrorFields(): Boolean {
        val errors: List<CharSequence> = mutableListOf(
            binding.fieldName.error,
            binding.fieldEmail.error,
        ).filterNotNull()

        return errors.isNotEmpty()
    }

    private fun doRegister() {
        if (listOf(
                hasEmptyFields(), hasErrorFields()
            ).contains(true)
        ) {
            Snackbar.make(
                this.binding.root,
                this.getString(R.string.silahkan_mengisi_field_diatas_terlebih_dahulu),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val nameReqBody = binding.fieldName.editText?.text.toString().toRequestBody()
        val emailReqBody = binding.fieldEmail.editText?.text.toString().toRequestBody()

        this.profileEditViewModel.profileEdit(
            context = this, profileEditRequest = ProfileEditRequest(
                name = nameReqBody,
                email = emailReqBody,
            )
        )
    }

}