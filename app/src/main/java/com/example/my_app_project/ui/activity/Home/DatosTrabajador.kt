package com.example.my_app_project.ui.activity.Home

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.R
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatosTrabajador : AppCompatActivity() {
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_trabajador)

        val profesion = findViewById<EditText>(R.id.profesion)
        val especialidad = findViewById<EditText>(R.id.especialidad)
        val descripcion = findViewById<EditText>(R.id.descripcion)
        val horario = findViewById<EditText>(R.id.horario)
        val tarifa = findViewById<EditText>(R.id.tarifa)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        viewModel.cargarUsuario()

        btnRegistrar.setOnClickListener {
            viewModel.usuario.value?.let { usuarioActual ->
                val usuarioActualizado = usuarioActual.copy(
                    profesion = profesion.text.toString().ifBlank { null },
                    especialidad = especialidad.text.toString().ifBlank { null },
                    descripcion = descripcion.text.toString().ifBlank { null },
                    horario = horario.text.toString().ifBlank { null },
                    tarifa = tarifa.text.toString().ifBlank { null },
                    esColaborador = true
                )

                viewModel.guardarUsuario(usuarioActualizado)

                Toast.makeText(this, "¡Ahora eres trabajador!", Toast.LENGTH_SHORT).show()
                finish()
            } ?: run {
                Toast.makeText(this, "Usuario no cargado aún", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}