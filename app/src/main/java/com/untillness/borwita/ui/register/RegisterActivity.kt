package com.untillness.borwita.ui.register

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityRegisterBinding
import com.untillness.borwita.helpers.Unfocus

class RegisterActivity : Unfocus() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        this.binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        supportActionBar?.hide()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.triggers()
    }

    private fun triggers() {
        binding.apply {
            buttonLogin.setOnClickListener {
                finish()
            }

            buttonRegister.setOnClickListener {
//                doRegister()
            }
        }
    }
}