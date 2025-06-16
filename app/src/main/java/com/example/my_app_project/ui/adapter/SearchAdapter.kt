package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ItemTrabajadorBinding
import com.example.my_app_project.domain.model.ServicioUser

class SearchAdapter(
    private var listaTrabajadores: List<ServicioUser>,
    private val onFavoritoClickListener: OnFavoritoClickListener
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    class SearchViewHolder(val binding: ItemTrabajadorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemTrabajadorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    interface OnFavoritoClickListener {
        fun onFavoritoClick(servicio: ServicioUser)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val trabajador = listaTrabajadores[position]
        with(holder.binding) {
            txtNombre.text = trabajador.nombreUserperfil
            textcategoria.text = trabajador.categoriaUserperfil
            txtDescripcion.text = trabajador.especialidadUserperfil
            txtRating.text = trabajador.calificacionUser

            Glide.with(holder.itemView.context)
                .load(trabajador.imagenUserperfil)
                .placeholder(R.drawable.ic_persona)
                .into(imgTrabajador)

            btnFavorito.setImageResource(
                if (trabajador.esFavorito) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite
            )

            btnFavorito.setOnClickListener {
                onFavoritoClickListener.onFavoritoClick(trabajador)
            }
        }
    }

    override fun getItemCount(): Int = listaTrabajadores.size

    fun actualizarLista(nuevaLista: List<ServicioUser>) {
        listaTrabajadores = nuevaLista
        notifyDataSetChanged()
    }

    fun actualizarFavorito(uid: String, esFavorito: Boolean) {
        val index = listaTrabajadores.indexOfFirst { it.uidUserperfil == uid }
        if (index != -1) {
            listaTrabajadores[index].esFavorito = esFavorito
            notifyItemChanged(index)
        }
    }
}