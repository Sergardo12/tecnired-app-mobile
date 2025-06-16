package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentHistoryBinding
import com.example.my_app_project.ui.adapter.HistorialAdapter
import com.example.my_app_project.R
import com.example.my_app_project.presentation.historial.HistorialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistorialAdapter
    private val viewModel: HistorialViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HistorialAdapter()
        binding.recyclerHistorial.layoutManager = LinearLayoutManager(context)
        binding.recyclerHistorial.adapter = adapter

        viewModel.cargarHistorial()

        viewModel.historial.observe(viewLifecycleOwner) {
            adapter.setData(it)
        }

        // Buscar por categoría mientras escribe
        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrarPorCategoria(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Ordenar por fecha más reciente
        binding.btnReciente.setOnClickListener {
            viewModel.ordenarPorFecha(descendente = true)
        }

        // Ordenar por fecha más antigua
        binding.btnAntiguo.setOnClickListener {
            viewModel.ordenarPorFecha(descendente = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
