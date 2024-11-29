package com.untillness.borwita.ui.profile_password

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.untillness.borwita.data.remote.requests.ProfilePasswordRequest
<<<<<<< HEAD
import com.untillness.borwita.data.states.AppState
=======
import com.untillness.borwita.data.states.ApiState
>>>>>>> 97dcba9 (Initial commit)
import com.untillness.borwita.databinding.ActivityProfilePasswordBinding
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.widgets.AppDialog
import okhttp3.RequestBody.Companion.toRequestBody

class ProfilePasswordActivity : Unfocus() {
    private lateinit var binding: ActivityProfilePasswordBinding
    private lateinit var profilePasswordViewModel: ProfilePasswordViewModel
    private lateinit var appDialog: AppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityProfilePasswordBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.profilePasswordViewModel =
            ViewModelFactory.obtainViewModel<ProfilePasswordViewModel>(this)

        ViewCompat.setOnApplyWindowInsetsListener(this.binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true);

        title = "Ubah Kata Sandi"
        this.appDialog = AppDialog(this)

        this.triggers()

        this.listeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    private fun triggers() {
        binding.apply {
            buttonSave.setOnClickListener {
                doChangePassword()
            }
        }
    }

    private fun hasEmptyFields(): Boolean {
        val fieldText: List<Int> = mutableListOf(
            binding.fieldPasswordOld.editText?.text?.length ?: 0,
            binding.fieldPasswordNew.editText?.text?.length ?: 0,
            binding.fieldPasswordConfirm.editText?.text?.length ?: 0,
        )

        return fieldText.contains(0)
    }

    private fun hasErrorFields(): Boolean {
        val errors: List<CharSequence> = mutableListOf(
            binding.fieldPasswordOld.error,
            binding.fieldPasswordNew.error,
            binding.fieldPasswordConfirm.error,
        ).filterNotNull()

        return errors.isNotEmpty()
    }

    private fun doChangePassword() {
        if (this.binding.fieldPasswordOld.editText?.text.toString().isEmpty()) {
            this.binding.fieldPasswordOld.error = "Kata Sandi Lama wajib diisi."
        }
        if (this.binding.fieldPasswordNew.editText?.text.toString().isEmpty()) {
            this.binding.fieldPasswordNew.error = "Kata Sandi Baru wajib diisi."
        }
        if (this.binding.fieldPasswordConfirm.editText?.text.toString().isEmpty()) {
            this.binding.fieldPasswordConfirm.error = "Konfirmasi Kata Sandi wajib diisi."
        }

        if (binding.fieldPasswordNew.editText?.text.toString() != binding.fieldPasswordConfirm.editText?.text.toString()) {
            this.binding.fieldPasswordConfirm.error =
                "Kata Sandi konfirmasi tidak sama, silahkan periksa kembali."
        }

        if (listOf(
                hasEmptyFields(), hasErrorFields()
            ).contains(true)
        ) {
            return
        }

        val oldPassword = binding.fieldPasswordOld.editText?.text.toString().toRequestBody()
        val newPassword = binding.fieldPasswordNew.editText?.text.toString().toRequestBody()

        this.profilePasswordViewModel.doPasswordEdit(
            context = this, req = ProfilePasswordRequest(
                newPassword = newPassword,
                oldPassword = oldPassword,
            )
        )
    }

    private fun listeners() {

        this.profilePasswordViewModel.apply {
            passwordEditState.observe(this@ProfilePasswordActivity) {
                when (it) {
<<<<<<< HEAD
                    AppState.Loading -> {
                        appDialog.showLoadingDialog()
                    }

                    AppState.Standby -> {
                        appDialog.hideLoadingDialog()
                    }

                    is AppState.Error -> {
=======
                    ApiState.Loading -> {
                        appDialog.showLoadingDialog()
                    }

                    ApiState.Standby -> {
                        appDialog.hideLoadingDialog()
                    }

                    is ApiState.Error -> {
>>>>>>> 97dcba9 (Initial commit)
                        appDialog.hideLoadingDialog()
                        AppDialog.error(
                            this@ProfilePasswordActivity, message = it.message
                        )
                    }

<<<<<<< HEAD
                    is AppState.Success<*> -> {
=======
                    is ApiState.Success<*> -> {
>>>>>>> 97dcba9 (Initial commit)
                        appDialog.hideLoadingDialog()
                        AppDialog.success(
                            this@ProfilePasswordActivity,
                            message = "Berhasil mengubah kata sandi.",
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

}