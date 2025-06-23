package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ActivityPerfilUsuarioBinding
import com.example.my_app_project.presentation.perfilUser.PerfilUserViewModel
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PerfilUsuario : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilUsuarioBinding
    private val PerfilUserViewModel: PerfilUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uid = intent.getStringExtra("uid") ?: return
        PerfilUserViewModel.cargarPerfil(uid)
        PerfilUserViewModel.perfilUser.observe(this) { usuario ->
            usuario?.let {
                binding.txtNombre.text = it.nombreUserperfil
                binding.txtCategoria.text = it.categoriaUserperfil
                binding.txtEspecialidad.text = it.especialidadUserperfil
                binding.txtTarifa.text = "⭐ ${it.puntajeUserperfil}"
                binding.txtTelefono.text = it.numeroUserperfil
                binding.txtCorreo.text = it.correoUserperfil
                Glide.with(this).load(it.imagenUserperfil).circleCrop().into(binding.ivFotoPerfil)
            }
        }
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
        binding.btnMetodoPago.setOnClickListener{
            val uid = intent.getStringExtra("uid") ?: return@setOnClickListener
            val intent = Intent(this, MetodoPago::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        binding.btnCompartir.setOnClickListener {
            mostrarDialogoCompartir()
        }
    }
    private fun mostrarDialogoCompartir() {
        val nombre = binding.txtNombre.text.toString()
        val categoria = binding.txtCategoria.text.toString()
        val especialidad = binding.txtEspecialidad.text.toString()
        val telefono = binding.txtTelefono.text.toString()
        val correo = binding.txtCorreo.text.toString()

        val mensaje = """
        ¡Hola! Te comparto el perfil de un profesional:
        
        👤 Nombre: $nombre
        🛠 Categoría: $categoria
        🔧 Especialidad: $especialidad
        📞 Teléfono: $telefono
        ✉️ Correo: $correo
        
    """.trimIndent()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }
        startActivity(Intent.createChooser(intent, "Compartir perfil vía"))
    }
}