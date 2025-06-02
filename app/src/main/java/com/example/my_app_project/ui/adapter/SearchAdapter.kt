package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ItemTrabajadorBinding
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.domain.model.Trabajador

class SearchAdapter(private var listaTrabajadores: List<ServicioPost>) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: ItemTrabajadorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemTrabajadorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val trabajador = listaTrabajadores[position]
        with(holder.binding) {
            txtNombre.text = trabajador.nombreUsuarioServicioPost
            textcategoria.text = trabajador.categoriaServicioPost
            txtDescripcion.text = trabajador.descripcionServicioPost
            txtRating.text = trabajador.tarifaServicioPost
            // Glide o Picasso
            Glide.with(holder.itemView.context)
                .load(trabajador.imagenServicioPost)
                .placeholder(R.drawable.ic_persona)
                .into(imgTrabajador)
        }
    }

    override fun getItemCount(): Int = listaTrabajadores.size

    fun actualizarLista(nuevaLista: List<ServicioPost>) {
        listaTrabajadores = nuevaLista
        notifyDataSetChanged()
    }
}
