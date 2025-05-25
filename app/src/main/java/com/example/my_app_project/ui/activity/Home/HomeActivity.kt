package com.example.my_app_project.ui.activity.Home

import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import com.example.my_app_project.R
import com.example.my_app_project.utils.safeNavigate
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawer = findViewById<NavigationView>(R.id.navigation_view)
        val headerView = drawer.getHeaderView(0)

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 0.7).toInt()
        drawer.layoutParams.width = drawerWidth
        drawer.requestLayout()

        val btnDatosPersonales = headerView.findViewById<Button>(R.id.btn_datos_personales)
        val btnDatosCuenta = headerView.findViewById<Button>(R.id.btn_datos_cuenta)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        val toggle = ActionBarDrawerToggle(
//            this,
//            drawerLayout,
//            toolbar,
//            R.string.navigation_drawer_open,
//            R.string.navigation_drawer_close
//        )
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.black)

        val customTitle = findViewById<TextView>(R.id.custom_title)
        val iconProfile = findViewById<ImageView>(R.id.icon_profile)
        val iconNotifications = findViewById<ImageView>(R.id.icon_notifications)

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
            customTitle.text = when (destination.id) {
                R.id.historyFragment -> "Historial"
                R.id.searchFragment -> "Buscar"
                R.id.homeFragment -> "Inicio"
                R.id.notificationsFragment -> "Notificaciones"
                R.id.favoritesFragment -> "Favoritos"
                else -> ""
            }

            if (destination.id == R.id.notificationsFragment) {
                iconProfile.setImageResource(R.drawable.ic_arrow_back)
            } else {
                iconProfile.setImageResource(R.drawable.ic_profile)
            }
        }

        iconProfile.setOnClickListener {
            if (navController.currentDestination?.id == R.id.notificationsFragment) {
                navController.popBackStack()
            } else {
                drawerLayout.openDrawer(drawer)
            }
        }

        iconNotifications.setOnClickListener {
            navController.safeNavigate(R.id.notificationsFragment)
        }

        // Acciones para los botones del Drawer (puedes personalizar)
        btnDatosPersonales.setOnClickListener {
            // Acción al pulsar "Datos personales"
        }

        btnDatosCuenta.setOnClickListener {
            // Acción al pulsar "Datos de la cuenta"
        }
    }
}
