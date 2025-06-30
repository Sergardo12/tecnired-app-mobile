package com.example.my_app_project.ui.activity.Register

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.my_app_project.R
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import com.example.my_app_project.ui.activity.Home.DatosPersonales
import com.example.my_app_project.ui.activity.Home.HomeActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.getValue

class FormInicioSesion : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1
    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_inicio_sesion)

        auth = FirebaseAuth.getInstance()
        configurarGoogleSignIn()

        val usuario = FirebaseAuth.getInstance().currentUser
        if (usuario != null && usuario.isEmailVerified) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        val recuperarClave = findViewById<TextView>(R.id.textRecuperarClave)

        recuperarClave.setOnClickListener {
            val correo = findViewById<EditText>(R.id.email).text.toString().trim()

            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(correo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        val emailEdit = findViewById<EditText>(R.id.email)
        val passEdit = findViewById<EditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.btnIngresar)
        val btnGmail = findViewById<ImageView>(R.id.btngmail)
        val btnMostrar = findViewById<ImageButton>(R.id.btnMostrarContraseña)
        var mostrar = false

        btnMostrar.setOnClickListener {
            mostrar = !mostrar
            if (mostrar) {
                passEdit.transformationMethod = null
                btnMostrar.setBackgroundResource(R.drawable.ojoabierto)
            } else {
                passEdit.transformationMethod = PasswordTransformationMethod.getInstance()
                btnMostrar.setBackgroundResource(R.drawable.ojocerrado)
            }
            passEdit.setSelection(passEdit.text.length)
        }

        btnLogin.setOnClickListener {
            val correo = emailEdit.text.toString().trim()
            val clave = passEdit.text.toString().trim()

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (clave.length < 6) {
                Toast.makeText(this, "Contraseña mínima de 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, clave)
                .addOnSuccessListener {
                    val usuario = auth.currentUser
                    if (usuario?.isEmailVerified == true) {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Debes verificar tu correo antes de continuar", Toast.LENGTH_LONG).show()
                        usuario?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this, "Correo de verificación reenviado", Toast.LENGTH_SHORT).show()
                            }
                        auth.signOut()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnGmail.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = task.getResult(ApiException::class.java)
                val idToken = cuenta.idToken
                autenticarConFirebase(idToken)
            } catch (e: ApiException) {
                e.printStackTrace()
                Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun autenticarConFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credencial)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuario = auth.currentUser
                    Toast.makeText(this, "Bienvenido, ${usuario?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show()
                    Log.e("AuthFirebase", "Fallo en la autenticación", task.exception)
                }
            }
    }

    private fun configurarGoogleSignIn() {
        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, opciones)
    }
}
