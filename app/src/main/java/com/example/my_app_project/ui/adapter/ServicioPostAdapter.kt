package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.ServicioPost

/**
 * Adaptador para mostrar una lista de publicaciones de servicios (ServicioPost) en un RecyclerView.
 */
class ServicioPostAdapter : ListAdapter<ServicioPost, ServicioPostAdapter.ServicioPostViewHolder>(DiffCallback()) {

    /**
     * Crea una nueva instancia del ViewHolder inflando el layout del ítem de servicio.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_servicio_post, parent, false)
        return ServicioPostViewHolder(view)
    }

    /**
     * Enlaza los datos del servicio a la vista del ViewHolder en la posición especificada.
     */
    override fun onBindViewHolder(holder: ServicioPostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder que representa un ítem individual de la lista de publicaciones de servicios.
     */
    class ServicioPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Referencias a los elementos visuales del layout
        private val imagen: ImageView = itemView.findViewById(R.id.imgServicio)
        private val nombreUsuario: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val categoria: TextView = itemView.findViewById(R.id.categoriaServicioPost)
        private val tarifa: TextView = itemView.findViewById(R.id.tvTarifa)

        /**
         * Asigna los datos del ServicioPost a los elementos visuales del ítem.
         */
        fun bind(servicio: ServicioPost) {
            // Cargar la imagen del servicio usando Glide
            Glide.with(itemView.context)
                .load(servicio.imagenServicioPost)
                .into(imagen)

            // Mostrar nombre completo del usuario
            val nombreCompleto = "${servicio.nombreUsuarioServicioPost} "
            nombreUsuario.text = nombreCompleto

            // Formatear tarifa con símbolo monetario
            val tarifaconSimbolo = "s./ ${servicio.tarifaServicioPost}.00"

            // Mostrar el resto de la información
            descripcion.text = servicio.descripcionServicioPost
            categoria.text = servicio.categoriaServicioPost
            tarifa.text = tarifaconSimbolo
        }
    }

    /**
     * Comparador para optimizar el rendimiento del RecyclerView detectando cambios entre ítems.
     */
    class DiffCallback : DiffUtil.ItemCallback<ServicioPost>() {

        /**
         * Determina si dos objetos representan el mismo ítem (por nombre completo + descripción).
         * Idealmente se usaría un ID único si estuviera disponible.
         */
        override fun areItemsTheSame(oldItem: ServicioPost, newItem: ServicioPost): Boolean {
            return oldItem.nombreUsuarioServicioPost == newItem.nombreUsuarioServicioPost &&

                    oldItem.descripcionServicioPost == newItem.descripcionServicioPost
        }

        /**
         * Determina si el contenido de dos ítems es exactamente igual.
         */
        override fun areContentsTheSame(oldItem: ServicioPost, newItem: ServicioPost): Boolean {
            return oldItem == newItem
        }
    }
}
