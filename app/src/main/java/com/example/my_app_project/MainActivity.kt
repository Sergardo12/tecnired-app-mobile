package com.example.my_app_project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.presentation.historial.HistorialViewModel
import com.example.my_app_project.ui.activity.Home.HomeActivity
import com.example.my_app_project.ui.activity.Register.FormInicioSesion
import com.example.my_app_project.ui.activity.Register.FormRegisCliente
import com.example.my_app_project.ui.fragment.CategoryFragment
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val viewModel: HistorialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        supportFragmentManager.beginTransaction()
//            .replace(android.R.id.content, CategoryFragment())
//            .commit()


        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//        val currentUser = FirebaseAuth.getInstance().currentUser
//        if (currentUser != null) {
//            Log.d("MainActivity", "Usuario autenticado: ${currentUser.uid}")
//            viewModel.crearHistorial()
//        } else {
//            Log.d("MainActivity", "No hay usuario autenticado aún")
//        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Boton Registrar
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            val intento = Intent(this, FormRegisCliente::class.java)
            startActivity(intento)
        }

        //Boton Iniciar Sesion
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        btnIniciarSesion.setOnClickListener{
            val intent = Intent(this, FormInicioSesion::class.java)
            startActivity(intent)
        }



//        startActivity(Intent(this, HomeActivity::class.java))
//        finish()


    }
}