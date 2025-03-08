package com.example.app_development_final_project

import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
        supportActionBar?.title = "Watch It"

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.main_bottom_bar)

        navController = navHostFragment?.navController
        navController?.let {
            val mainFragments = listOf(R.id.feedFragment, R.id.addPostFragment, R.id.profilePageFragment)
            val allFragments = mainFragments + listOf(R.id.signInFragment, R.id.signUpFragment)

            NavigationUI.setupActionBarWithNavController(activity = this, navController = it)
            NavigationUI.setupWithNavController(navigationBarView = bottomNavigationView, navController = it)

            val appBarConfiguration = AppBarConfiguration(allFragments.toSet())
            NavigationUI.setupActionBarWithNavController(activity = this, navController = it, configuration = appBarConfiguration)

            it.addOnDestinationChangedListener { _, destination, _ ->
                bottomNavigationView.visibility = if (destination.id in mainFragments) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                navController?.popBackStack()
                true
            }

            else -> navController?.let {
                NavigationUI.onNavDestinationSelected(item, it)
            } ?: false
        }
    }
}