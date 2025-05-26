package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemFavoritosBinding
import com.example.my_app_project.domain.model.Favorito
import android.widget.Toast
import com.example.my_app_project.R

class FavoritosAdapter(private val listaFavoritos: List<Favorito>) :
    RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder>() {

    inner class FavoritosViewHolder(val binding: ItemFavoritosBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val binding = ItemFavoritosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
        val favorito = listaFavoritos[position]

        holder.binding.imgTrabajador.setImageResource(favorito.imagen)
        holder.binding.txtNombre.text = favorito.nombre
        holder.binding.textcategoria.text = favorito.categoria
        holder.binding.txtDescripcion.text = favorito.descripcion
        holder.binding.txtRating.text = favorito.rating.toString()

        var esFavorito = true
        if (esFavorito) {   holder.binding.btnFavorito.setImageResource(R.drawable.ic_favorite_filled)}
        else {holder.binding.btnFavorito.setImageResource(R.drawable.ic_favorite)}

        holder.binding.btnFavorito.setOnClickListener {
            esFavorito = !esFavorito

            val nuevoIcono = if (esFavorito) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
            holder.binding.btnFavorito.setImageResource(nuevoIcono)

            val mensaje = if (esFavorito) "Añadido a favoritos" else "Eliminado de favoritos"
            Toast.makeText(holder.itemView.context, mensaje, Toast.LENGTH_SHORT).show()


        }

    }

    override fun getItemCount(): Int = listaFavoritos.size
}