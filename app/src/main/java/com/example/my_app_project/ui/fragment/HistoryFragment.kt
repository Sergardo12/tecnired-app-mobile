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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistorialViewModel by viewModels()
    private lateinit var adapter: HistorialAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = HistorialAdapter(
            onEliminarClick = { historialItem ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar servicio")
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
            }
        )

        binding.recyclerHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistorial.adapter = adapter

        // Observamos los datos filtrados
        viewModel.historialFiltrado.observe(viewLifecycleOwner) { lista ->
            adapter.submitList(lista)
        }

        // Escuchar cambios en el EditText
        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.filtrarPorCategoria(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
