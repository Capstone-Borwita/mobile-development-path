package com.untillness.borwita

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.untillness.borwita.databinding.ActivityMainBinding
import com.untillness.borwita.helpers.ViewModelFactory
import com.untillness.borwita.ui.login.LoginActivity
import com.untillness.borwita.ui.wrapper.WrapperActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        supportActionBar?.hide()

        mainViewModel = ViewModelFactory.obtainViewModel<MainViewModel>(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val isLogin: Boolean = mainViewModel.isLogin()

            val intent = if (isLogin) {
                Intent(this, WrapperActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            startActivity(intent)
            finish()
        }, 2000)
    }
}