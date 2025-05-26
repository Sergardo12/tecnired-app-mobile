package com.example.my_app_project.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentHistoryBinding
import com.example.my_app_project.domain.model.HistorialItem
import com.example.my_app_project.ui.adapter.HistorialAdapter
import com.example.my_app_project.R

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var listaHistorial: List<HistorialItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Datos de ejemplo
        listaHistorial = listOf(
            HistorialItem("Gasfitería", "06:43 PM", "Completado", "- S/50.00", R.drawable.ic_persona),
            HistorialItem("Electricidad", "04:32 PM", "Pendiente", "- S/30.00", R.drawable.ic_persona),
            HistorialItem("Carpintería", "02:15 PM", "Cancelado", "- S/0.00", R.drawable.ic_profile)
        )

        historialAdapter = HistorialAdapter(listaHistorial)
        binding.recyclerHistorial.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHistorial.adapter = historialAdapter

        return binding.root
    }
}
