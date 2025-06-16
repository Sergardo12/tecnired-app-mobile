package com.example.my_app_project.ui.activity.Register

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.R
import com.example.my_app_project.ui.activity.Home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.util.Log
class FormRegisCliente : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form_regis_cliente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        configurarGoogleSignIn()

        val btnGmail = findViewById<ImageView>(R.id.btngmail)
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
            }
        }
    }

    private fun autenticarConFirebase(idToken: String?) {
        val credencial = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credencial)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val usuario = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(this, "Bienvenido, ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la pantalla actual para que no regrese con el boton de "atrás"
                } else {
                    // Error al autenticar
                    Toast.makeText(this, "Error al autenticar con Google", Toast.LENGTH_SHORT).show()
                    Log.e("AuthFirebase", "Fallo en la autenticación", task.exception)
                }
            }
    }

    private fun configurarGoogleSignIn(){
        val opciones = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Lo toma de google-services.json
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, opciones)
    }
}