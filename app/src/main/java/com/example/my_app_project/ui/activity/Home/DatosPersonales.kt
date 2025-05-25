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
import androidx.lifecycle.ViewModelProvider
import com.example.my_app_project.R
import com.example.my_app_project.ui.viewmodel.UsuarioViewModel
import com.example.my_app_project.domain.model.Usuario

class DatosPersonales : AppCompatActivity() {

    private lateinit var viewModel: UsuarioViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_personales)

        val editNombre = findViewById<EditText>(R.id.nombre)
        val editApellido = findViewById<EditText>(R.id.apellido)
        val editTelefono = findViewById<EditText>(R.id.telefono)

        viewModel = ViewModelProvider(this).get(UsuarioViewModel::class.java)
        viewModel.cargarUsuario()
        viewModel.usuario.observe(this) { usuario ->
            usuario?.let {
                editNombre.setText(it.nombre)
                editApellido.setText(it.apellido)
                editTelefono.setText(it.telefono)
            }
        }
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        btnGuardar.setOnClickListener {
            if (editNombre.text.isBlank() || editApellido.text.isBlank() || editTelefono.text.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(
                nombre = editNombre.text.toString(),
                apellido = editApellido.text.toString(),
                telefono = editTelefono.text.toString()
            )
            viewModel.guardarUsuario(nuevoUsuario)
            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}