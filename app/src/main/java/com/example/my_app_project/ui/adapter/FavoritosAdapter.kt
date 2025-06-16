package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemFavoritosBinding
import com.example.my_app_project.domain.model.Favorito
import android.widget.Toast
import com.example.my_app_project.R
import com.bumptech.glide.Glide
import com.example.my_app_project.domain.model.UsuarioFavorito
import com.example.my_app_project.presentation.favoritos.FavoritosViewModel

class FavoritosAdapter(
    private var listaFavoritos: List<UsuarioFavorito>,
    private val viewModel: FavoritosViewModel,
    private val uidUsuario: String
) : RecyclerView.Adapter<FavoritosAdapter.FavoritosViewHolder>()
 {

     inner class FavoritosViewHolder(val binding: ItemFavoritosBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritosViewHolder {
        val binding = ItemFavoritosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoritosViewHolder(binding)
    }

     override fun onBindViewHolder(holder: FavoritosViewHolder, position: Int) {
         val favorito = listaFavoritos[position]

         with(holder.binding) {
             txtNombre.text = favorito.nombre
             textcategoria.text = favorito.categoria
             txtDescripcion.text = favorito.descripcion
             txtRating.text = favorito.rating.toString()

             Glide.with(holder.itemView.context)
                 .load(favorito.imagenUrl)
                 .placeholder(R.drawable.ic_persona)
                 .into(imgTrabajador)

             btnFavorito.setImageResource(R.drawable.ic_favorite_filled)

             btnFavorito.setOnClickListener {
                 viewModel.toggleFavorito(uidUsuario, Favorito(favorito.uid), esFavorito = true) {
                     eliminarFavoritoPorId(favorito.uid)
                     Toast.makeText(holder.itemView.context, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                 }
             }

         }
     }


     override fun getItemCount(): Int = listaFavoritos.size
     fun obtenerFavoritoEn(pos: Int): UsuarioFavorito = listaFavoritos[pos]

     fun actualizarLista(nuevaLista: List<UsuarioFavorito>) {
         listaFavoritos = nuevaLista
         notifyDataSetChanged()
     }

     fun eliminarFavoritoPorId(uid: String) {
         val index = listaFavoritos.indexOfFirst { it.uid == uid }
         if (index != -1) {
             listaFavoritos = listaFavoritos.toMutableList().also { it.removeAt(index) }
             notifyItemRemoved(index)
         }
     }



 }
