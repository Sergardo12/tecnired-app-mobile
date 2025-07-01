package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.MensajeChat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val uidActual: String) : ListAdapter<MensajeChat, RecyclerView.ViewHolder>(MensajeDiffCallback()) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).emisorId == uidActual) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == 1) R.layout.item_msg_derecha else R.layout.item_msg_izquierda
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MensajeViewHolder).bind(getItem(position))
    }

    inner class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(mensaje: MensajeChat) {
            itemView.findViewById<TextView>(R.id.tvMensaje).text = mensaje.contenido
            val tvHora = itemView.findViewById<TextView>(R.id.tvHora)
            val horaFormateada = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(mensaje.timestamp))
            tvHora.text = horaFormateada
        }
    }

    class MensajeDiffCallback : DiffUtil.ItemCallback<MensajeChat>() {
        override fun areItemsTheSame(old: MensajeChat, new: MensajeChat): Boolean = old.id == new.id
        override fun areContentsTheSame(old: MensajeChat, new: MensajeChat): Boolean = old == new
    }
}
