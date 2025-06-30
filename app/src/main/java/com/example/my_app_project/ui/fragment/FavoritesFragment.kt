package com.example.my_app_project.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentFavoritesBinding
import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.ui.adapter.FavoritosAdapter
import com.example.my_app_project.presentation.favoritos.FavoritosViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.ui.activity.Home.PerfilUsuario

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritosViewModel by viewModels()
    private lateinit var favoritosAdapter: FavoritosAdapter
    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        uid = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root

        favoritosAdapter = FavoritosAdapter(
            emptyList(),
            viewModel,
            uid
        ) { favorito ->
            val intent = Intent(requireContext(), PerfilUsuario::class.java)
            intent.putExtra("uidColaborador", favorito.uid)
            startActivity(intent)
        }

        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavoritos.adapter = favoritosAdapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val favorito = favoritosAdapter.obtenerFavoritoEn(position)
                favorito.let {
                    viewModel.toggleFavorito(uid, Favorito(it.uid), esFavorito = true) {
                        favoritosAdapter.eliminarFavoritoPorId(it.uid)
                        Toast.makeText(requireContext(), "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerFavoritos)

        viewModel.favoritos.observe(viewLifecycleOwner) { lista ->
            if (lista.isEmpty()) {
                binding.recyclerFavoritos.visibility = View.GONE
                binding.textNoFavoritos.visibility = View.VISIBLE
            } else {
                binding.recyclerFavoritos.visibility = View.VISIBLE
                binding.textNoFavoritos.visibility = View.GONE
                favoritosAdapter.actualizarLista(lista)
            }
        }



        binding.btnGasfitero.setOnClickListener { viewModel.filtrarPorCategoria("Gasfitería") }
        binding.btnElectricista.setOnClickListener { viewModel.filtrarPorCategoria("Electricidad") }
        binding.btnCarpintero.setOnClickListener { viewModel.filtrarPorCategoria("Carpinteria") }
        binding.btnComputo.setOnClickListener { viewModel.filtrarPorCategoria("Cómputo") }
        binding.btnMecanico.setOnClickListener { viewModel.filtrarPorCategoria("Mecánica") }
        binding.btnReposteria.setOnClickListener { viewModel.filtrarPorCategoria("Repostería") }
        binding.btnall.setOnClickListener { viewModel.mostrarTodos()}

        viewModel.cargarFavoritos(uid)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

