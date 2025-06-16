package com.example.my_app_project.ui.activity.ServiciosPost

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.R
import com.example.my_app_project.ui.fragment.ServiciosPostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServiciosPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activarPantallaCompleta()
        setContentView(R.layout.activity_servicios_post)
        aplicarMargenesDeSistema()

        if (savedInstanceState == null) {
            val categoriaSeleccionada = obtenerCategoriaDesdeIntent()
            cargarFragmentoServiciosPost(categoriaSeleccionada)
        }
    }

    // Habilita pantalla completa sin barras del sistema
    private fun activarPantallaCompleta() {
        enableEdgeToEdge()
    }

    // Aplica márgenes según barras del sistema (arriba, abajo, etc.)
    private fun aplicarMargenesDeSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barrasDelSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(
                barrasDelSistema.left,
                barrasDelSistema.top,
                barrasDelSistema.right,
                barrasDelSistema.bottom
            )
            insets
        }
    }

    // Obtiene la categoría seleccionada enviada por intent
    private fun obtenerCategoriaDesdeIntent(): String? {
        return intent.getStringExtra("categoria_seleccionada")
    }

    // Carga el fragmento y le pasa la categoría seleccionada
    private fun cargarFragmentoServiciosPost(categoria: String?) {
        val fragmento = ServiciosPostFragment().apply {
            arguments = Bundle().apply {
                putString("categoria_seleccionada", categoria)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerServicios, fragmento)
            .commit()
    }
}
