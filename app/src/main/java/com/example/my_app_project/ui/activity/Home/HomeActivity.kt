package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
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
import com.example.my_app_project.presentation.historial.HistorialViewModel
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import com.example.my_app_project.ui.activity.Register.FormInicioSesion
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    val viewModel: HistorialViewModel by viewModels()
    val userviewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModel.crearHistorial()
        userviewModel.cargarUsuario()

        val rootView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.root_layout)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.updatePadding(top = statusBarHeight)
            insets
        }

        val esFlujoInicial = intent.getBooleanExtra("esFlujoInicial", false)

        val uidser = FirebaseAuth.getInstance().currentUser?.uid ?: return
        userviewModel.verificarPerfilUsuario(uidser)

        userviewModel.tienePerfil.observe(this) { tienePerfil ->
            if (!tienePerfil) {
                val intent = Intent(this, DatosPersonales::class.java)
                intent.putExtra("esFlujoInicial", true)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            }
        }

        userviewModel.usuario.observe(this) { usuario ->
            usuario?.let {
                val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
                when (usuario.rol) {
                    "colaborador" -> {
                        bottomNav.menu.clear()
                        bottomNav.inflateMenu(R.menu.bottom_nav_colaborador)
                    }
                    else -> {
                        bottomNav.menu.clear()
                        bottomNav.inflateMenu(R.menu.bottom_nav_cliente)
                    }
                }
            }
        }



        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val drawer = findViewById<NavigationView>(R.id.navigation_view)
        val headerView = drawer.getHeaderView(0)
        val nombreUsuario = headerView.findViewById<TextView>(R.id.txtNombreUsuario)

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("Usuario").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nombre = document.getString("nombre") ?: ""
                        val apellido = document.getString("apellido") ?: ""
                        nombreUsuario.text = "$nombre $apellido"
                    }
                }
                .addOnFailureListener { e ->
                    nombreUsuario.text = "Usuario"
                }
        }

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val drawerWidth = (screenWidth * 0.7).toInt()
        drawer.layoutParams.width = drawerWidth
        drawer.requestLayout()

        val btnDatosPersonales = headerView.findViewById<Button>(R.id.btn_datos_personales)
        val btnDatosTrabajador = headerView.findViewById<Button>(R.id.btn_datos_trabajador)
        val btnDatosCuenta = headerView.findViewById<Button>(R.id.btn_datos_cuenta)
        val btnCerrarSesion = headerView.findViewById<Button>(R.id.btn_cerrar_sesion)
        val btnFavoritos = headerView.findViewById<Button>(R.id.btn_favoritos)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                R.id.solicitarServicioFragment -> {
                    navController.safeNavigate(R.id.solicitarServicioFragment)
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
                R.id.solicitarServicioFragment -> "Solicitar Servicio"
                R.id.homeFragment -> "Inicio"
                R.id.notificationsFragment -> "Notificaciones"
                R.id.favoritesFragment -> "Favoritos"
                R.id.solicitudesColaboradorFragment -> "Solicitudes"
                else -> ""
            }

            if (destination.id == R.id.notificationsFragment) {
                iconProfile.setImageResource(R.drawable.ic_arrow_back)
            } else {
                iconProfile.setImageResource(R.drawable.menu_3barras)
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

        btnDatosPersonales.setOnClickListener {
            val intent = Intent(this, DatosPersonales::class.java)
            intent.putExtra("esFlujoInicial", false)
            startActivity(intent)
        }
        btnDatosTrabajador.setOnClickListener {
            val intent = Intent(this, DatosTrabajador::class.java)
            startActivity(intent)
        }
        btnDatosCuenta.setOnClickListener{
            val intent = Intent(this,PerfilUsuario::class.java)
            startActivity(intent)
        }
        btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, FormInicioSesion::class.java))
            finish()
        }

        btnFavoritos.setOnClickListener {
            navController.safeNavigate(R.id.favoritesFragment)
            drawerLayout.closeDrawer(drawer)
        }

    }
}