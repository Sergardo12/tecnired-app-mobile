package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.Categoria


class CategoriasAdapter : ListAdapter<Categoria, CategoriasAdapter.CategoriaViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item_categoria, parent, false)
        return CategoriaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagen: ImageButton = itemView.findViewById(R.id.image_categoria)
        private val texto: TextView = itemView.findViewById(R.id.text_categoria)

        fun bind(categoria: Categoria) {
            texto.text = categoria.nombreCategoria
            Glide.with(itemView.context).load(categoria.imagenCategoria).into(imagen)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Categoria>() {
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem.nombreCategoria == newItem.nombreCategoria
        }

        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem == newItem
        }
    }
}



