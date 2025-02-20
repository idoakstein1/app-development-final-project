package com.example.app_development_final_project

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bottom_bar)

        navController = navHostFragment?.navController
        navController?.let {
            NavigationUI.setupActionBarWithNavController(activity = this, navController = it)
            NavigationUI.setupWithNavController(navigationBarView = bottomNavigationView, navController = it)

            val appBarConfiguration = AppBarConfiguration(setOf(R.id.feedFragment, R.id.addPostFragment, R.id.profilePageFragment))
            NavigationUI.setupActionBarWithNavController(activity = this, navController = it, configuration = appBarConfiguration)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navController?.popBackStack()
            else -> navController?.let {
                NavigationUI.onNavDestinationSelected(item, it)
            }
        }

        return true
    }
}