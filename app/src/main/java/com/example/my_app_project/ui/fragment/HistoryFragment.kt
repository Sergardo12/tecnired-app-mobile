package com.example.my_app_project.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentHistoryBinding
import com.example.my_app_project.ui.adapter.HistorialAdapter
import com.example.my_app_project.R
import com.example.my_app_project.presentation.historial.HistorialViewModel
import com.example.my_app_project.ui.activity.Servicios.ServicioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var adapter: HistorialAdapter

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obtenerRolUsuario { rol ->
            configurarAdapter(rol)
        }

        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.filtrarPorCategoria(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun configurarAdapter(rolUsuario: String) {
        adapter = HistorialAdapter(
            rolUsuario = rolUsuario,
            onEliminarClick = { historialItem ->
                AlertDialog.Builder(requireContext())
                    .setTitle("|ADVERTENCIA¡")
                    .setMessage("¿Seguro que quieres eliminar el servicio?")
                    .setPositiveButton("Aceptar") { _, _ ->
                        viewModel.eliminarServicio(historialItem.id) { exito ->
                            if (!exito) {
                                Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },
            onActualizarEstado = { servicioId, nuevoEstado ->
                FirebaseFirestore.getInstance()
                    .collection("servicios_solicitados")
                    .document(servicioId)
                    .update("estado", nuevoEstado)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Estado actualizado", Toast.LENGTH_SHORT).show()
                        viewModel.cargarHistorial()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            }
        )
        binding.recyclerHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistorial.adapter = adapter

        viewModel.historialFiltrado.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }
    }
    private fun obtenerRolUsuario(callback: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("usuarios")
            .document(uid)
            .collection("userData")
            .document("perfil")
            .get()
            .addOnSuccessListener { document ->
                val rol = document.getString("rol") ?: "cliente"
                callback(rol)
            }
            .addOnFailureListener {
                callback("cliente") // fallback por si falla
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

