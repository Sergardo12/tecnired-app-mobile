package com.example.my_app_project.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.my_app_project.databinding.FragmentSearchBinding
import com.example.my_app_project.ui.adapter.SearchAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.presentation.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.domain.model.ServicioUser
import com.example.my_app_project.presentation.favoritos.FavoritosViewModel
import com.google.firebase.auth.FirebaseAuth

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val favoritosViewModel: FavoritosViewModel by viewModels()
    private lateinit var adapter: SearchAdapter
    private var favoritosIds: Set<String> = emptySet()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val uidUsuario = FirebaseAuth.getInstance().currentUser?.uid

        adapter = SearchAdapter(emptyList(), object : SearchAdapter.OnFavoritoClickListener {
            override fun onFavoritoClick(servicio: ServicioUser) {
                uidUsuario?.let {
                    val favorito = Favorito(uid = servicio.uidUserperfil)
                    val nuevoEstado = !servicio.esFavorito

                    favoritosViewModel.toggleFavorito(it, favorito, esFavorito = servicio.esFavorito) {
                        servicio.esFavorito = nuevoEstado
                        adapter.actualizarFavorito(servicio.uidUserperfil, nuevoEstado)
                        val mensaje = if (nuevoEstado) {
                            "Agregado a favoritos"
                        } else {
                            "Eliminado de favoritos"
                        }
                        Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        binding.recyclerBusqueda.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBusqueda.adapter = adapter

        favoritosViewModel.favoritos.observe(viewLifecycleOwner) { lista ->
            favoritosIds = lista.map { it.uid }.toSet()
            viewModel.obtenerTodosLosServicios(favoritosIds)
        }

        viewModel.servicios.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }

        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val texto = s.toString().trim()
                if (texto.isNotEmpty()) {
                    viewModel.buscarPorTexto(texto, favoritosIds)
                } else {
                    viewModel.obtenerTodosLosServicios(favoritosIds)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnGasfitero.setOnClickListener {
            viewModel.buscarPorCategoria("gasfiteria", favoritosIds)
        }
        binding.btnElectricista.setOnClickListener {
            viewModel.buscarPorCategoria("electricidad", favoritosIds)
        }
        binding.btnCarpintero.setOnClickListener {
            viewModel.buscarPorCategoria("carpinteria", favoritosIds)
        }
        binding.btnall.setOnClickListener {
            viewModel.obtenerTodosLosServicios(favoritosIds)
        }

        uidUsuario?.let {
            favoritosViewModel.cargarFavoritos(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

