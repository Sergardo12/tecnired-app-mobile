package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemHistoryBinding
import com.example.my_app_project.domain.model.HistorialItem

class HistorialAdapter(private val listaHistorial: List<HistorialItem>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    inner class HistorialViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistorialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = listaHistorial[position]
        holder.binding.txtNombreServicio.text = item.nombreServicio
        holder.binding.txtHoraFecha.text = item.horaFecha
        holder.binding.txtEstado.text = item.estado
        holder.binding.txtMonto.text = item.monto
        holder.binding.iconoServicio.setImageResource(item.icono)
    }

    override fun getItemCount(): Int = listaHistorial.size
}