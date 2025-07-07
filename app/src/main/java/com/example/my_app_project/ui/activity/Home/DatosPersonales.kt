package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.R
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import com.example.my_app_project.domain.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatosPersonales : AppCompatActivity() {

    private val viewModel: UsuarioViewModel by viewModels()

    private val REQUEST_IMAGE_PICK = 1001
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_datos_personales)

        val esFlujoInicial = intent.getBooleanExtra("esFlujoInicial", false)




        val editNombre = findViewById<EditText>(R.id.nombre)
        val editApellido = findViewById<EditText>(R.id.apellido)
        val editTelefono = findViewById<EditText>(R.id.telefono)
        val btnGuardar = findViewById<Button>(R.id.btn_guardar)
        val prueba1 = ""

        val imageButton = findViewById<ImageButton>(R.id.logo)
        imageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(this, "No has iniciado sesión", Toast.LENGTH_SHORT).show()

            finish()
            return
        }

        // Cargar datos actuales si existen
        viewModel.cargarUsuario()
        viewModel.usuario.observe(this) { usuario ->
            usuario?.let {
                editNombre.setText(it.nombre)
                editApellido.setText(it.apellido)
                editTelefono.setText(it.telefono)
            }
        }

        // Guardar
        btnGuardar.setOnClickListener {
            val nombre = editNombre.text.toString().trim()
            val apellido = editApellido.text.toString().trim()
            val telefono = editTelefono.text.toString().trim()
            val rolActual = viewModel.usuario.value?.rol ?: "cliente"


            if (nombre.isBlank() || apellido.isBlank() || telefono.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoUsuario = Usuario(
                nombre = nombre,
                apellido = apellido,
                telefono = telefono,
                rol = rolActual
            )

            if (imageUri != null) {
                viewModel.guardarUsuarioConFoto(imageUri!!, nuevoUsuario) { exito ->
                    if (exito) {
                        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                        if (esFlujoInicial) {
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                viewModel.guardarUsuario(nuevoUsuario)
                Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                if (esFlujoInicial) {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                finish()
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            imageUri = data?.data
            findViewById<ImageButton>(R.id.logo).setImageURI(imageUri)
        }
    }



}
