package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemNotiBinding
import com.example.my_app_project.domain.model.Notificacion

class NotiAdapter(private val lista: List<Notificacion>) :
    RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {

    inner class NotiViewHolder(val binding: ItemNotiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val binding = ItemNotiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = lista[position]
        holder.binding.tituloNotificacion.text = item.titulo
        holder.binding.descripcionNotificacion.text = item.descripcion
        holder.binding.horaNotificacion.text = item.hora
        holder.binding.iconoNotificacion.setImageResource(item.icono)

        holder.binding.dotLeido.visibility = if (!item.leido) View.VISIBLE else View.GONE
    }
}
