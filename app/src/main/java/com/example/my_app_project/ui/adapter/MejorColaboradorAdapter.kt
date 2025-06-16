package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.MejorColaborador

/**
 * Adaptador para mostrar una lista de los mejores colaboradores en un RecyclerView.
 */
class MejorColaboradorAdapter(
    private val onContactarClick: (MejorColaborador) -> Unit
) : ListAdapter<MejorColaborador, MejorColaboradorAdapter.MejorColaboradorViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MejorColaboradorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mejor_colaborador, parent, false)
        return MejorColaboradorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MejorColaboradorViewHolder, position: Int) {
        holder.bind(getItem(position), onContactarClick)
    }

    class MejorColaboradorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imagen: ImageView = itemView.findViewById(R.id.imgColaborador)
        private val nombre: TextView = itemView.findViewById(R.id.tvNombreColaborador)
        private val categoria: TextView = itemView.findViewById(R.id.tvOficioColaborador)
        private val puntaje: TextView = itemView.findViewById(R.id.tvPuntajeColaborador)
        private val botonContactar: Button = itemView.findViewById(R.id.btnContactar)

        fun bind(colaborador: MejorColaborador, onContactarClick: (MejorColaborador) -> Unit) {
            // Cargar imagen con Glide
            Glide.with(itemView.context)
                .load(colaborador.imagenUserperfil)
                .placeholder(R.drawable.item_persona)
                .into(imagen)

            nombre.text = colaborador.nombreUserperfil
            categoria.text = colaborador.categoriaUserperfil
            puntaje.text = "⭐ ${colaborador.puntajeUserperfil ?: "0.0"}"


            botonContactar.setOnClickListener {
                onContactarClick(colaborador)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MejorColaborador>() {
        override fun areItemsTheSame(oldItem: MejorColaborador, newItem: MejorColaborador): Boolean {
            return oldItem.nombreUserperfil == newItem.nombreUserperfil &&
                    oldItem.categoriaUserperfil == newItem.categoriaUserperfil
        }

        override fun areContentsTheSame(oldItem: MejorColaborador, newItem: MejorColaborador): Boolean {
            return oldItem == newItem
        }
    }
}
