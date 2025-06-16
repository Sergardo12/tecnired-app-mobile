package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ItemHistoryBinding
import com.example.my_app_project.domain.model.HistorialItem

class HistorialAdapter : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    private val lista = mutableListOf<HistorialItem>()

    fun setData(nuevaLista: List<HistorialItem>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgPerfil = v.findViewById<ImageView>(R.id.imgPerfil)
        val txtNombre = v.findViewById<TextView>(R.id.txtNombre)
        val txtCategoria = v.findViewById<TextView>(R.id.txtCategoria)
        val txtFecha = v.findViewById<TextView>(R.id.txtFecha)
        val txtEstado = v.findViewById<TextView>(R.id.txtEstado)
        val txtPrecio = v.findViewById<TextView>(R.id.txtPrecio)

        fun bind(item: HistorialItem) {
            txtNombre.text = item.nombreHistorial
            txtCategoria.text = item.categoriaHistorial
            txtFecha.text = item.fechaFinalizadoHistorial
            txtEstado.text = item.estadoHistorial
            txtPrecio.text = "- S/ ${item.precioHistorial}"
            Glide.with(imgPerfil.context).load(item.imagenHistorial).into(imgPerfil)
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): ViewHolder {
        val view = LayoutInflater.from(p.context).inflate(R.layout.item_history, p, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(h: ViewHolder, i: Int) = h.bind(lista[i])
    override fun getItemCount(): Int = lista.size
}