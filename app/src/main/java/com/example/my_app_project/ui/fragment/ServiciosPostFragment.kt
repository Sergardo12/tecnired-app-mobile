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
import com.example.my_app_project.databinding.FragmentServiciosPostBinding
import com.example.my_app_project.presentation.serviciosPost.ServicioPostViewModel
import com.example.my_app_project.ui.adapter.ServicioPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ServiciosPostFragment : Fragment() {

    private var _binding: FragmentServiciosPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServicioPostViewModel by viewModels()
    private lateinit var adapter: ServicioPostAdapter
    private var categoriaSeleccionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiciosPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarRecyclerView()
        recuperarCategoriaSeleccionada()
        observarErrores()
        observarListaServicios()
    }

    // Inicializa el RecyclerView y su adaptador
    private fun inicializarRecyclerView() {
        adapter = ServicioPostAdapter()
        binding.recyclerServiciosPost.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ServiciosPostFragment.adapter
        }
    }

    // Recupera el argumento "categoria_seleccionada" que se pasó desde la actividad
    private fun recuperarCategoriaSeleccionada() {
        categoriaSeleccionada = arguments?.getString("categoria_seleccionada")
    }

    // Observa los errores emitidos por el ViewModel
    private fun observarErrores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { mensajeError ->
                    mensajeError?.let {
                        Log.e("ServiciosPostFragment", "Error: $it")
                    }
                }
            }
        }
    }

    // Observa la lista de servicios y la filtra según la categoría seleccionada
    private fun observarListaServicios() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serviciosPost.collect { listaCompleta ->
                    categoriaSeleccionada?.let { categoria ->
                        val listaFiltrada = listaCompleta.filter {
                            it.categoriaServicioPost.equals(categoria, ignoreCase = true)
                        }
                        Log.d("ServiciosPostFragment", "Filtrado por '$categoria': $listaFiltrada")
                        adapter.submitList(listaFiltrada)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
