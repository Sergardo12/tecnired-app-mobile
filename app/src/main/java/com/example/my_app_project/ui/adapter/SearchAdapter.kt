package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemTrabajadorBinding
import com.example.my_app_project.domain.model.Trabajador

class SearchAdapter(private val listaTrabajadores: List<Trabajador>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: ItemTrabajadorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemTrabajadorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val trabajador = listaTrabajadores[position]

        with(holder.binding) {
            txtNombre.text = trabajador.nombre
            textcategoria.text = trabajador.categoria
            txtDescripcion.text = trabajador.descripcion
            txtRating.text = trabajador.rating.toString()
            imgTrabajador.setImageResource(trabajador.imagen)

        }
    }

    override fun getItemCount(): Int = listaTrabajadores.size
}