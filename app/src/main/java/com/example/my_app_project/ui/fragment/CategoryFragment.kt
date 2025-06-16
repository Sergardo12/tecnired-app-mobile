package com.example.my_app_project.ui.fragment

import android.content.Intent
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
import com.example.my_app_project.ui.activity.ServiciosPost.ServiciosPostActivity
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

        configurarRecyclerView()
        observarErrores()
        observarCategorias()
    }

    // 🧩 Configura el RecyclerView y el Adapter con su acción de clic
    private fun configurarRecyclerView() {
        categoriasAdapter = CategoriasAdapter { categoria ->
            irAActividadServicios(categoria.nombreCategoria)
        }

        binding.recyclerCategoria.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoriasAdapter
        }
    }

    // 📤 Navega a la actividad de servicios con la categoría seleccionada
    private fun irAActividadServicios(nombreCategoria: String) {
        val intent = Intent(requireContext(), ServiciosPostActivity::class.java).apply {
            putExtra("categoria_seleccionada", nombreCategoria)
        }
        startActivity(intent)
    }

    // ❌ Observa posibles errores del ViewModel
    private fun observarErrores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { errorMsg ->
                    errorMsg?.let {
                        Log.e("CategoryFragment", "Error al obtener categorías: $it")
                    }
                }
            }
        }
    }

    // ✅ Observa el flujo de categorías y actualiza el Adapter
    private fun observarCategorias() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoria.collect { categorias ->
                    Log.d("CategoryFragment", "Categorías recibidas: $categorias")
                    categoriasAdapter.submitList(categorias)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object
}
