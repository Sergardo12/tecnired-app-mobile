package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ActivityMetodoPagoBinding
import com.example.my_app_project.presentation.perfilUser.PerfilUserViewModel
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import com.example.my_app_project.ui.activity.Register.FormInicioSesion
import com.example.my_app_project.ui.activity.Home.PerfilUsuario
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MetodoPago : AppCompatActivity() {
    private lateinit var binding: ActivityMetodoPagoBinding
    private val PerfilUserViewModel: PerfilUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMetodoPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uid = intent.getStringExtra("uid") ?: return
        PerfilUserViewModel.cargarPerfil(uid)
        PerfilUserViewModel.perfilUser.observe(this) { usuario ->
            usuario?.let {
                binding.txtYape.text = it.nombreUserperfil
                binding.txtPlin.text = it.nombreUserperfil
                binding.txtNumero.text = it.numeroUserperfil
            }
        }

        binding.btnVolver.setOnClickListener {
            startActivity(Intent(this, PerfilUsuario::class.java))
            finish()
        }
    }
}