package com.example.my_app_project.ui.adapter

import android.R.attr.onClick
import com.example.my_app_project.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.databinding.ItemNotiBinding
import com.example.my_app_project.domain.model.Notificacion

class NotiAdapter(
    private var lista: List<Notificacion>,
    private val onClick: (Notificacion) -> Unit
) : RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {

    inner class NotiViewHolder(val binding: ItemNotiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val binding = ItemNotiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotiViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = lista[position]
        with(holder.binding) {
            tituloNotificacion.text = item.titulo
            descripcionNotificacion.text = item.mensaje

            try {
                horaNotificacion.text = android.text.format.DateFormat.format("dd MMM, HH:mm", item.fecha)
            } catch (e: Exception) {
                horaNotificacion.text = "Fecha desconocida"
            }

            Glide.with(holder.itemView.context)
                .load(item.icono.takeIf { it.isNotEmpty() } ?: R.drawable.ic_persona)
                .into(iconoNotificacion)

            dotLeido.visibility = if (!item.leido) View.VISIBLE else View.GONE
        }
        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun actualizarLista(nuevaLista: List<Notificacion>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}

