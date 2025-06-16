package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ActivityPerfilUsuarioBinding
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PerfilUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilUsuarioBinding
    private val usuarioViewModel: UsuarioViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioViewModel.usuario.observe(this) { usuario ->
            usuario?.let {
                binding.txtNombre.text = "${it.nombre} ${it.apellido}"
                binding.txtCorreo.text = it.correo
                binding.txtTelefono.text = it.telefono
                binding.txtProfesion.text = it.profesion ?: ""
                binding.txtEspecialidad.text = it.especialidad ?: ""
                binding.txtDescripcion.text = it.descripcion ?: ""
                binding.txtHorario.text = it.horario ?: ""
                binding.txtTarifa.text = "S/ ${it.tarifa ?: "0.00"}"
            }
        }
        usuarioViewModel.cargarUsuario()

        binding.btnVolver.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLlamar.setOnClickListener {
            val telefono = binding.txtTelefono.text.toString()
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$telefono")
            }
            startActivity(intent)
        }
    }
}