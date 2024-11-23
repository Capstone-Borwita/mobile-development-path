package com.untillness.borwita.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.untillness.borwita.R
import com.untillness.borwita.data.remote.requests.LoginRequest
import com.untillness.borwita.data.states.AppState
import com.untillness.borwita.databinding.ActivityLoginBinding
import com.untillness.borwita.helpers.AppHelpers
import com.untillness.borwita.helpers.Unfocus
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.register.RegisterActivity
import com.untillness.borwita.ui.wrapper.WrapperActivity
import com.untillness.borwita.widgets.AppDialog

class LoginActivity : Unfocus() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var appDialog: AppDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.loginViewModel = ViewModelFactory.obtainViewModel<LoginViewModel>(this)
        this.appDialog = AppDialog(this)

        this.triggers()
        this.listeners()
    }

    private fun listeners() {
        this.loginViewModel.apply {
            this.loginState.observe(this@LoginActivity) {
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
                            this@LoginActivity, message = it.message
                        )
                    }

                    is AppState.Success<*> -> {
                        appDialog.hideLoadingDialog()
                        AppDialog.success(
                            this@LoginActivity,
                            message = getString(R.string.berhasil_masuk),
                            callback = object : AppDialog.Companion.AppDialogCallback {

                                override fun onDismiss() {
                                    val intent =
                                        Intent(this@LoginActivity, WrapperActivity::class.java)
                                    startActivity(intent)
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
        this.binding.apply {
            this.buttonLogin.setOnClickListener {
                doLogin()
            }
            this.buttonRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun hasEmptyFields(): Boolean {
        val fieldText: List<Int> = mutableListOf(
            binding.fieldEmail.editText?.text?.length ?: 0,
            binding.fieldPassword.editText?.text?.length ?: 0,
        )

        return fieldText.contains(0)
    }

    private fun hasErrorFields(): Boolean {
        val errors: List<CharSequence> = mutableListOf(
            binding.fieldEmail.error, binding.fieldPassword.error
        ).filterNotNull()

        return errors.isNotEmpty()
    }

    private fun doLogin() {
        if (listOf(
                hasEmptyFields(), hasErrorFields()
            ).contains(true)
        ) {
            Snackbar.make(
                this.binding.root,
                getString(R.string.silahkan_mengisi_field_diatas_terlebih_dahulu),
                Snackbar.LENGTH_SHORT
            ).show()
            return
        }

        val req = LoginRequest(
            email = AppHelpers.makeRequestBody(binding.fieldEmail.editText?.text.toString()),
            password = AppHelpers.makeRequestBody(binding.fieldPassword.editText?.text.toString()),
        )

        loginViewModel.doLogin(
            context = this,
            request = req,
        )
    }
}