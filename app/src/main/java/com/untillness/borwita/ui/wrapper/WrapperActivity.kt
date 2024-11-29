package com.untillness.borwita.ui.wrapper

<<<<<<< HEAD
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
=======
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
>>>>>>> 97dcba9 (Initial commit)
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
<<<<<<< HEAD
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityWrapperBinding


=======
import com.untillness.borwita.R
import com.untillness.borwita.databinding.ActivityWrapperBinding

>>>>>>> 97dcba9 (Initial commit)
class WrapperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWrapperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityWrapperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_wrapper)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }
}