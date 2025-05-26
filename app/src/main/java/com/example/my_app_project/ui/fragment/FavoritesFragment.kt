package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentFavoritesBinding
import com.example.my_app_project.domain.model.Favorito
import com.example.my_app_project.ui.adapter.FavoritosAdapter
import com.example.my_app_project.R

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var favoritosAdapter: FavoritosAdapter
    private lateinit var listaFavoritos: MutableList<Favorito>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        // Datos de ejemplo
        listaFavoritos = mutableListOf(
            Favorito("Luis R.", "Gasfitero", "Gasfitería en el hogar", 4.5f, R.drawable.ic_persona),
            Favorito("Juan P.", "Electricista", "Instalaciones eléctricas", 4.2f, R.drawable.user_perfil),
            Favorito("Pedro S.", "Carpintero", "Muebles a medida", 4.8f, R.drawable.ic_persona)
        )

        favoritosAdapter = FavoritosAdapter(listaFavoritos)
        binding.recyclerFavoritos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavoritos.adapter = favoritosAdapter

        return binding.root
    }
}
