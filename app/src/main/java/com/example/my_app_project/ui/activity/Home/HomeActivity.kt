package com.example.my_app_project.ui.activity.Home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.my_app_project.R
import com.example.my_app_project.utils.safeNavigate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    navController.safeNavigate(R.id.homeFragment)
                    true
                }
                R.id.searchFragment -> {
                    navController.safeNavigate(R.id.searchFragment)
                    true
                }
                R.id.historyFragment -> {
                    navController.safeNavigate(R.id.historyFragment)
                    true
                }
                R.id.favoritesFragment -> {
                    navController.safeNavigate(R.id.favoritesFragment)
                    true
                }
                else -> false
            }
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = when (destination.id) {
                R.id.historyFragment -> "Historial"
                R.id.searchFragment -> "Buscar"
                R.id.homeFragment -> "Inicio"
                R.id.notificationsFragment -> "Notificaciones"
                R.id.favoritesFragment -> "Favoritos"
                else -> ""
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return when (item.itemId) {
            R.id.action_notifications -> {
                navController.safeNavigate(R.id.notificationsFragment)
                true
            }
            R.id.action_profile -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}