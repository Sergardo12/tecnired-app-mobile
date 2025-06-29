package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.databinding.ItemServiceBinding
import com.example.my_app_project.domain.model.ServicioSolicitud
import java.util.concurrent.TimeUnit

class SolicitudColaboradorAdapter(
    private var lista: List<Pair<ServicioSolicitud, Double>>,
    private val onItemClick: (ServicioSolicitud, Double) -> Unit
) : RecyclerView.Adapter<SolicitudColaboradorAdapter.SolicitudViewHolder>() {

    inner class SolicitudViewHolder(val binding: ItemServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SolicitudViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: SolicitudViewHolder, position: Int) {
        val (solicitud, distancia) = lista[position]

        holder.binding.descripcionService.text = solicitud.descripcion
        holder.binding.distanciaService.text = "${"%.2f".format(distancia)} km"
        holder.binding.fechaService.text = obtenerTiempoRelativo(solicitud.fechaCreacion)

        holder.itemView.setOnClickListener {
            onItemClick(solicitud, distancia)
        }
    }

    fun actualizarLista(nuevaLista: List<Pair<ServicioSolicitud, Double>>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    private fun obtenerTiempoRelativo(fechaCreacion: Long): String {
        val tiempoActual = System.currentTimeMillis()
        val diferencia = tiempoActual - fechaCreacion

        val minutos = TimeUnit.MILLISECONDS.toMinutes(diferencia)
        val horas = TimeUnit.MILLISECONDS.toHours(diferencia)
        val dias = TimeUnit.MILLISECONDS.toDays(diferencia)

        return when {
            minutos < 1 -> "hace un momento"
            minutos < 60 -> "hace $minutos min"
            horas < 24 -> "hace $horas h"
            else -> "hace $dias días"
        }
    }
}
