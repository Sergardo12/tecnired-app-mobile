package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.HistorialItem

class HistorialAdapter(
    private val onEliminarClick: (HistorialItem) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    private var listaHistorial = listOf<HistorialItem>()

    fun submitList(lista: List<HistorialItem>) {
        listaHistorial = lista
        notifyDataSetChanged()
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        private val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnElminar)

        fun bind(item: HistorialItem) {
            txtNombre.text = item.nombreCliente
            txtCategoria.text = item.categoria
            txtDescripcion.text = item.descripcion
            txtFecha.text = item.fechaCreacion
            txtEstado.text = item.estado

            btnEliminar.setOnClickListener {
                onEliminarClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        holder.bind(listaHistorial[position])
    }

    override fun getItemCount(): Int = listaHistorial.size
}


