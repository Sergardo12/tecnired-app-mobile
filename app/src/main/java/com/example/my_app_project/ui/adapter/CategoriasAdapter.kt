package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.Categoria

/**
 * Adaptador para mostrar una lista de categorías en un RecyclerView.
 * Recibe una función de callback para manejar los clics en cada categoría.
 */
class CategoriasAdapter(
    private val alHacerClickEnCategoria: (Categoria) -> Unit
) : ListAdapter<Categoria, CategoriasAdapter.CategoriaViewHolder>(ComparadorCategorias()) {

    /**
     * Crea una nueva instancia del ViewHolder inflando el layout correspondiente.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item_categoria, parent, false)
        return CategoriaViewHolder(vista, alHacerClickEnCategoria)
    }

    /**
     * Enlaza los datos de la categoría a la vista correspondiente.
     */
    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        holder.enlazar(getItem(position))
    }

    /**
     * ViewHolder personalizado que representa cada ítem de categoría.
     */
    class CategoriaViewHolder(
        vistaItem: View,
        private val alHacerClickEnCategoria: (Categoria) -> Unit
    ) : RecyclerView.ViewHolder(vistaItem) {

        // Referencias a los elementos de la vista
        private val imagenCategoria: ImageButton = vistaItem.findViewById(R.id.image_categoria)
        private val textoCategoria: TextView = vistaItem.findViewById(R.id.text_categoria)

        /**
         * Enlaza los datos de una categoría a los elementos visuales del ViewHolder.
         */
        fun enlazar(categoria: Categoria) {
            mostrarTextoCategoria(categoria)
            cargarImagenCategoria(categoria)
            configurarClick(categoria)
        }

        /**
         * Muestra el nombre de la categoría en el TextView.
         */
        private fun mostrarTextoCategoria(categoria: Categoria) {
            textoCategoria.text = categoria.nombreCategoria
        }

        /**
         * Carga la imagen de la categoría en el ImageButton usando Glide.
         */
        private fun cargarImagenCategoria(categoria: Categoria) {
            Glide.with(itemView.context)
                .load(categoria.imagenCategoria)
                .into(imagenCategoria)
        }

        /**
         * Configura el evento de clic sobre la imagen para notificar la selección.
         */
        private fun configurarClick(categoria: Categoria) {
            imagenCategoria.setOnClickListener {
                alHacerClickEnCategoria(categoria)
            }
        }
    }

    /**
     * Comparador utilizado por ListAdapter para optimizar actualizaciones de la lista.
     */
    class ComparadorCategorias : DiffUtil.ItemCallback<Categoria>() {

        /**
         * Determina si dos elementos representan la misma categoría.
         */
        override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem.nombreCategoria == newItem.nombreCategoria
        }

        /**
         * Determina si el contenido de dos categorías es exactamente igual.
         */
        override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
            return oldItem == newItem
        }
    }
}
