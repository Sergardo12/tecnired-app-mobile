package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentHomeCategoryServicesBinding
import com.example.my_app_project.presentation.categorias.CategoriaViewModel
import com.example.my_app_project.ui.adapter.CategoriasAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentHomeCategoryServicesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriaViewModel by viewModels()
    private lateinit var categoriasAdapter: CategoriasAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCategoryServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("CategoryFragment", "onViewCreated ejecutado")

        categoriasAdapter = CategoriasAdapter()

        binding.recyclerCategoria.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriasAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { errorMsg ->
                    errorMsg?.let {
                        Log.e("CategoryFragment", "Error al obtener categorias: $it")
                    }
                }
            }
        }

        // Recoger el flow correctamente
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoria.collect { categorias ->
                    Log.d("CategoryFragment", "Categorias recibidas: $categorias")
                    categoriasAdapter.submitList(categorias)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    companion object {

}
}