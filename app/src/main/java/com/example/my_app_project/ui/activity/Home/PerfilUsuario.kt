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

        usuarioViewModel.cargarUsuario()

        val uidColaborador = intent.getStringExtra("uidColaborador")

        if (uidColaborador != null) {
            usuarioViewModel.cargarPerfilPorUid(uidColaborador)
            usuarioViewModel.perfilColaborador.observe(this) { perfil ->
                perfil?.let {
                    binding.txtCategoria.text = it.categoriaUserperfil
                    binding.txtEspecialidad.text = it.especialidadUserperfil
                    binding.txtTarifa.text = it.puntajeUserperfil.toString()
                    binding.txtDescripcion.text = it.descripcionUserperfil
                    binding.txtHorario.text = it.horarioUserperfil
                    binding.txtNombre.text = it.nombreUserperfil
                    binding.txtTelefono.text = it.numeroUserperfil
                    binding.txtCorreo.text = it.correoUserperfil
                }
            }
        } else {
            usuarioViewModel.cargarPerfilColaborador()
            usuarioViewModel.usuario.observe(this) { usuario ->
                usuario?.let {
                    binding.txtNombre.text = "${it.nombre} ${it.apellido}"
                    binding.txtTelefono.text = it.telefono
                }
            }
            usuarioViewModel.perfilColaborador.observe(this) { perfil ->
                perfil?.let {
                    binding.txtCategoria.text = it.categoriaUserperfil
                    binding.txtEspecialidad.text = it.especialidadUserperfil
                    binding.txtTarifa.text = it.puntajeUserperfil.toString()
                    binding.txtDescripcion.text = it.descripcionUserperfil
                    binding.txtHorario.text = it.horarioUserperfil
                }
            }
        }




        binding.btnVolver.setOnClickListener {
            finish()
        }
        binding.btnLlamar.setOnClickListener {
            val telefono = binding.txtTelefono.text.toString()
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$telefono")
            }
            startActivity(intent)
        }
        binding.btnMetodoPago.setOnClickListener{
            val intent = Intent(this,MetodoPago::class.java)
            startActivity(intent)
        }
    }
}