package com.example.my_app_project.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentSearchBinding
import com.example.my_app_project.domain.model.Trabajador
import com.example.my_app_project.ui.adapter.SearchAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.presentation.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.text.Editable
import android.text.TextWatcher


@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        adapter = SearchAdapter(emptyList())
        binding.recyclerBusqueda.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBusqueda.adapter = adapter

        viewModel.servicios.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }

        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().trim()
                if (texto.isNotEmpty()) {
                    viewModel.buscarPorTexto(texto)
                } else {
                    viewModel.obtenerTodosLosServicios()
                }
            }


            override fun afterTextChanged(s: Editable?) {
            }
        })

        // Botones
        binding.btnGasfitero.setOnClickListener { viewModel.buscarPorCategoria("gasfiteria") }
        binding.btnElectricista.setOnClickListener { viewModel.buscarPorCategoria("electricidad") }
        binding.btnCarpintero.setOnClickListener { viewModel.buscarPorCategoria("carpinteria") }

        viewModel.obtenerTodosLosServicios()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}

