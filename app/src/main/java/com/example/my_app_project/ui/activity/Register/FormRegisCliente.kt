package com.example.my_app_project.ui.activity.Register

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.my_app_project.R
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import com.example.my_app_project.domain.model.UsuarioData
import com.google.firebase.firestore.FirebaseFirestore

class FormRegisCliente : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_regis_cliente)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nombreEdit = findViewById<EditText>(R.id.nombre)
        val apellidoEdit = findViewById<EditText>(R.id.apellido)
        val telefonoEdit = findViewById<EditText>(R.id.telefono)
        val emailEdit = findViewById<EditText>(R.id.email)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val passwordEdit = findViewById<EditText>(R.id.password)
        val btnMostrarPassword = findViewById<ImageButton>(R.id.btnMostrarPassword)
        var esVisible = false

        btnMostrarPassword.setOnClickListener {
            esVisible = !esVisible
            if (esVisible) {
                passwordEdit.transformationMethod = null
                btnMostrarPassword.setBackgroundResource(R.drawable.ojoabierto)
            } else {
                passwordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
                btnMostrarPassword.setBackgroundResource(R.drawable.ojocerrado)
            }
            passwordEdit.setSelection(passwordEdit.text.length)
        }


        btnRegistrar.setOnClickListener {
            val nombre = nombreEdit.text.toString().trim()
            val apellidos = apellidoEdit.text.toString().trim()
            val telefono = telefonoEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val password = passwordEdit.text.toString().trim()

            if (nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrarUsuario(email, password, UsuarioData(nombre, apellidos, telefono, email))
        }
    }

    private fun registrarUsuario(email: String, password: String, usuarioData: UsuarioData) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    firestore.collection("usuarios")
                        .document(uid)
                        .collection("userData")
                        .document("perfil")
                        .set(usuarioData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            auth.currentUser?.sendEmailVerification()
                                ?.addOnSuccessListener {
                                    Toast.makeText(this, "Verifica tu correo antes de iniciar sesión", Toast.LENGTH_LONG).show()
                                    auth.signOut()
                                    startActivity(Intent(this, FormInicioSesion::class.java))
                                    finish()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al guardar perfil", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al registrar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
