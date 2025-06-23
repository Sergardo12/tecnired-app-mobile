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
import com.example.my_app_project.databinding.FragmentMejoresColaboradoresBinding
import com.example.my_app_project.presentation.mejorColaborador.MejorColaboradorViewModel
import com.example.my_app_project.ui.activity.Home.PerfilUsuario
import com.example.my_app_project.ui.adapter.MejorColaboradorAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MejorColaboradorFragment : Fragment() {

    private var _binding: FragmentMejoresColaboradoresBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MejorColaboradorViewModel by viewModels()
    private lateinit var adapter: MejorColaboradorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMejoresColaboradoresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarRecyclerView()
        observarErrores()
        observarListaColaboradores()
        observarCargando()
    }

    private fun inicializarRecyclerView() {
        adapter = MejorColaboradorAdapter { colaborador ->
            val intent = Intent(requireContext(), PerfilUsuario::class.java).apply {
                putExtra("uid", colaborador.uidUserperfil)
            }
            startActivity(intent)
            Log.d("MejorColaboradorFragment", "Contactar a ${colaborador.nombreUserperfil}")
            // Puedes implementar lógica adicional aquí si deseas
        }

        binding.recyclerMejoresColaboradores.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MejorColaboradorFragment.adapter
        }
    }

    private fun observarErrores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { mensajeError ->
                    mensajeError?.let {
                        Log.e("MejorColaboradorFragment", "Error: $it")
                    }
                }
            }
        }
    }

    private fun observarListaColaboradores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mejoresColaboradores.collect { lista ->
                    Log.d("MejorColaboradorFragment", "Lista de mejores colaboradores: $lista")
                    adapter.submitList(lista)
                }
            }
        }
    }

    private fun observarCargando() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { estaCargando ->
                    binding.progressBar.visibility = if (estaCargando) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
