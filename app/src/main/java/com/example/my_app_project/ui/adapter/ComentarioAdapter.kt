package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemComentarioBinding
import com.example.my_app_project.domain.model.ComentarioPost

class ComentarioAdapter : RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    private val comentarios = mutableListOf<ComentarioPost>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val binding = ItemComentarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ComentarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        holder.bind(comentarios[position])
    }

    override fun getItemCount(): Int = comentarios.size

    fun submitList(nuevaLista: List<ComentarioPost>) {
        comentarios.clear()
        comentarios.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class ComentarioViewHolder(private val binding: ItemComentarioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comentario: ComentarioPost) {
            binding.tvNombreUsuarioComentario.text = comentario.nombreUsuario
            binding.tvContenidoComentario.text = comentario.contenido
        }
    }
}