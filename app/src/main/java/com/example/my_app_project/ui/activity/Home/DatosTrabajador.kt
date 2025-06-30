package com.example.my_app_project.ui.activity.Home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ActivityDatosTrabajadorBinding
import com.example.my_app_project.domain.model.UsuarioPerfil
import com.example.my_app_project.presentation.viewmodel.UsuarioViewModel
import com.example.my_app_project.ui.activity.Register.FormInicioSesion
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatosTrabajador : AppCompatActivity() {

    private lateinit var binding: ActivityDatosTrabajadorBinding
    private val viewModel: UsuarioViewModel by viewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatosTrabajadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editespecialidad = binding.especialidad
        val editdescripcion = binding.descripcion
        val edithorario = binding.horario

        viewModel.cargarUsuario()
        viewModel.cargarCategorias()

        viewModel.categorias.observe(this) { categorias ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategoria.adapter = adapter

            viewModel.cargarPerfilColaborador()
            viewModel.perfilColaborador.observe(this) { usuario ->
                usuario?.let {
                    editespecialidad.setText(it.especialidadUserperfil)
                    editdescripcion.setText(it.descripcionUserperfil)
                    edithorario.setText(it.horarioUserperfil)

                    val index = categorias.indexOf(it.categoriaUserperfil)
                    if (index >= 0) {
                        binding.spinnerCategoria.setSelection(index)
                    }
                }
            }
        }

        binding.btnRegistrar.setOnClickListener {
            val categoria = binding.spinnerCategoria.selectedItem?.toString()?.trim() ?: ""
            val especialidad = editespecialidad.text.toString().trim()
            val descripcion = editdescripcion.text.toString().trim()
            val horario = edithorario.text.toString().trim()

            val usuario = viewModel.usuario.value
            val uid = auth.currentUser?.uid ?: ""

            if (categoria.isNotEmpty() && especialidad.isNotEmpty() && usuario != null && uid.isNotEmpty()) {
                val perfil = UsuarioPerfil(
                    categoriaUserperfil = categoria,
                    correoUserperfil = auth.currentUser?.email ?: "",
                    especialidadUserperfil = especialidad,
                    imagenUserperfil = "",
                    descripcionUserperfil = descripcion,
                    horarioUserperfil = horario,
                    nombreUserperfil = usuario.nombre,
                    numeroUserperfil = usuario.telefono,
                    puntajeUserperfil = 0.0,
                    uid = uid
                )

                viewModel.guardarPerfilColaborador(uid, perfil)
                viewModel.registroExitoso.observe(this) { exitoso ->
                    if (exitoso) {
                        Toast.makeText(this, "Perfil registrado con éxito", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar el perfil", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
