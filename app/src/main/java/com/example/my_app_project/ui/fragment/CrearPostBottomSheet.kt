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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.my_app_project.presentation.serviciosPost.ServicioPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CrearPostBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogCrearPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServicioPostViewModel by viewModels()

    private var imagenUriSeleccionada: Uri? = null

    // 🔸 Launcher para seleccionar imagen de galería
    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUriSeleccionada = uri
            binding.ivPrevisualizarImagen.setImageURI(uri)
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

        // 📸 Botón para seleccionar imagen
        binding.btnSeleccionarImagen.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }

        // 📤 Botón para subir el post
        binding.btnSubirPost.setOnClickListener {
            val descripcion = binding.etDescripcion.text.toString().trim()
            val tarifa = binding.etTarifa.text.toString().trim()

            if (imagenUriSeleccionada != null && descripcion.isNotEmpty() && tarifa.isNotEmpty()) {
                val inputStream = requireContext().contentResolver.openInputStream(imagenUriSeleccionada!!)
                val imagenBytes = inputStream?.readBytes()

                if (imagenBytes != null) {
                    // ✅ Solo llama a la función, no envuelvas en otro scope
                    viewModel.publicarServicioPost(imagenBytes, descripcion, tarifa)

                    // ❌ NO hagas dismiss aquí, espera que ViewModel lo notifique
                } else {
                    Toast.makeText(requireContext(), "Error al leer la imagen", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Completa todos los campos y selecciona una imagen", Toast.LENGTH_SHORT).show()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.postCreadoExitosamente.collect { creado ->
                    if (creado) dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
