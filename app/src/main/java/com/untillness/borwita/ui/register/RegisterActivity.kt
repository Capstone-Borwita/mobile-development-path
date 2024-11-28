package com.untillness.borwita.ui.register

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.untillness.borwita.R
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityRegisterBinding
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.widgets.AppDialog
import okhttp3.RequestBody.Companion.toRequestBody

class RegisterActivity : Unfocus() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var appDialog: AppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(this.binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        this.registerViewModel = ViewModelFactory.obtainViewModel<RegisterViewModel>(this)
        this.appDialog = AppDialog(this)

        triggers()
        listeners()
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

        this.registerViewModel.apply {
            registerState.observe(this@RegisterActivity) {
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
                            this@RegisterActivity, message = it.message
                        )
                    }

                    is AppState.Success<*> -> {
                        appDialog.hideLoadingDialog()
                        AppDialog.success(
                            this@RegisterActivity,
                            message = getString(R.string.berhasil_mendaftar_silahkan_masuk_untuk_melanjutkan),
                            callback = object : AppDialog.Companion.AppDialogCallback {

                                override fun onDismiss() {
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


    private fun triggers() {
        binding.apply {
            buttonLogin.setOnClickListener {
                finish()
            }

            buttonRegister.setOnClickListener {
                doRegister()
            }
        }
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

        if (binding.fieldPassword.editText?.text.toString() != binding.fieldPasswordConfirm.editText?.text.toString()) {
            Snackbar.make(
                this.binding.root,
                "Kata Sandi konfirmasi tidak sama, silahkan periksa kembali.",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val nameReqBody = binding.fieldName.editText?.text.toString().toRequestBody()
        val emailReqBody = binding.fieldEmail.editText?.text.toString().toRequestBody()
        val passwordReqBody = binding.fieldPassword.editText?.text.toString().toRequestBody()

        this.registerViewModel.doRegister(
            context = this,
            name = nameReqBody,
            email = emailReqBody,
            password = passwordReqBody,
        )
    }

    private fun hasEmptyFields(): Boolean {
        val fieldText: List<Int> = mutableListOf(
            binding.fieldName.editText?.text?.length ?: 0,
            binding.fieldEmail.editText?.text?.length ?: 0,
            binding.fieldPassword.editText?.text?.length ?: 0,
            binding.fieldPasswordConfirm.editText?.text?.length ?: 0,
        )

        return fieldText.contains(0)
    }

    private fun hasErrorFields(): Boolean {
        val errors: List<CharSequence> = mutableListOf(
            binding.fieldName.error,
            binding.fieldEmail.error,
            binding.fieldPassword.error,
            binding.fieldPasswordConfirm.error,
        ).filterNotNull()

        return errors.isNotEmpty()
    }
}