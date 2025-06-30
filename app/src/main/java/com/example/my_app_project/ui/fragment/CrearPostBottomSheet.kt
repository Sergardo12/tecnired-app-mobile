package com.example.my_app_project.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import com.example.my_app_project.databinding.DialogCrearPostBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.my_app_project.presentation.serviciosPost.ServicioPostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CrearPostBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogCrearPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicioPostViewModel by viewModels()


    // 🔸 Aquí se guarda la URI de la imagen seleccionada
    private var imagenUriSeleccionada: Uri? = null

    // 🔸 Launcher para seleccionar la imagen de la galería
    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUriSeleccionada = uri
            binding.ivPrevisualizarImagen.setImageURI(uri) // Muestra la imagen en el ImageView
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCrearPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔘 Botón para seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }

        // Aquí luego colocarás el botón para subir el post
        // binding.btnSubirPost.setOnClickListener { ... }

        binding.btnSubirPost.setOnClickListener {
            val descripcion = binding.etDescripcion.text.toString().trim()
            val tarifa = binding.etTarifa.text.toString().trim()

            if (imagenUriSeleccionada != null && descripcion.isNotEmpty() && tarifa.isNotEmpty()) {
                val inputStream = requireContext().contentResolver.openInputStream(imagenUriSeleccionada!!)
                val bytes = inputStream?.readBytes() ?: return@setOnClickListener

                // Nombre de archivo para Cloudinary
                val nombreArchivo = "post_${System.currentTimeMillis()}.jpg"

                viewModel.subirImagenYCrearPost(bytes, nombreArchivo, descripcion, tarifa)
                dismiss() // Cierra el bottom sheet después de subir
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
