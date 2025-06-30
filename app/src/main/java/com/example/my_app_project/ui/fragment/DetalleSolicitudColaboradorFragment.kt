package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.my_app_project.databinding.FragmentDetalleSolicitudColaboradorBinding
import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.presentation.servicioSolicitud.ServicioSolicitudViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetalleSolicitudColaboradorFragment : Fragment() {

    private val viewModel: ServicioSolicitudViewModel by viewModels()
    private lateinit var binding: FragmentDetalleSolicitudColaboradorBinding
    private lateinit var solicitud: ServicioSolicitud

    private var distanciaRecibida: Double = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDetalleSolicitudColaboradorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener argumentos
        solicitud = requireArguments().getParcelable("solicitud") ?: return
        distanciaRecibida = requireArguments().getDouble("distancia", 0.0)

        // Mostrar datos básicos
        binding.tvDescripcion.text = solicitud.descripcion
        binding.tvDireccion.text = solicitud.direccion
        binding.tvDistancia.text = "%.2f km".format(distanciaRecibida)
        binding.tvFecha.text = formatearFecha(solicitud.fechaCreacion)

        // Cargar nombre del cliente
        obtenerNombreCliente(solicitud.clienteId)

        // Cargar nombre de la categoría
        obtenerNombreCategoria(solicitud.categoriaId)

        // Botón Aceptar
        binding.btnAceptar.setOnClickListener {
            val colaboradorId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            viewModel.aceptarSolicitud(solicitud.id, colaboradorId)
        }

        binding.btnVolver.setOnClickListener {
            findNavController().popBackStack()
        }

        observarEstadoAceptacion()
    }

    private fun observarEstadoAceptacion() {
        lifecycleScope.launch {
            viewModel.estadoAceptacion.collectLatest { resultado ->
                resultado?.onSuccess {
                    Toast.makeText(requireContext(), "Solicitud aceptada", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }?.onFailure {
                    Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun obtenerNombreCliente(clienteId: String) {
        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(clienteId)
            .collection("userData")
            .document("perfil")
            .get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: ""
                val apellido = doc.getString("apellido") ?: ""
                binding.tvCliente.text = "$nombre $apellido"
            }
            .addOnFailureListener {
                binding.tvCliente.text = "Nombre no disponible"
            }
    }

    private fun obtenerNombreCategoria(categoriaId: String) {
        FirebaseFirestore.getInstance()
            .collection("categorias")
            .document(categoriaId)
            .get()
            .addOnSuccessListener { doc ->
                val nombreCategoria = doc.getString("nombreCategoria") ?: "Sin nombre"
                binding.tvCategoria.text = nombreCategoria
            }
            .addOnFailureListener {
                binding.tvCategoria.text = "Categoría no disponible"
            }
    }

    private fun formatearFecha(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
