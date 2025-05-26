package com.example.my_app_project.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentSearchBinding
import com.example.my_app_project.domain.model.Trabajador
import com.example.my_app_project.ui.adapter.SearchAdapter
import androidx.recyclerview.widget.LinearLayoutManager


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var busquedaAdapter: SearchAdapter
    private lateinit var listaTrabajadores: List<Trabajador>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Datos de ejemplo
        listaTrabajadores = listOf(
            Trabajador("Luis R.", "Gasfitero", "Reparación de tuberías", 4.5f, R.drawable.ic_persona),
            Trabajador("Ana P.", "Electricista", "Instalaciones eléctricas", 4.0f, R.drawable.ic_persona),
            Trabajador("Pedro Z.", "Carpintero", "Muebles a medida", 4.8f, R.drawable.ic_persona),
            Trabajador("Carlos M.", "Mecánico", "Reparación de autos", 4.2f, R.drawable.ic_persona)
        )

        busquedaAdapter = SearchAdapter(listaTrabajadores)

        binding.recyclerBusqueda.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = busquedaAdapter
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}