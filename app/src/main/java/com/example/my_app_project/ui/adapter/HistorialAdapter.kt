package com.example.my_app_project.ui.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.HistorialItem

class HistorialAdapter(
    private val rolUsuario: String,
    private val onEliminarClick: (HistorialItem) -> Unit,
    private val onActualizarEstado: (String, String) -> Unit,
    private val onCalificar: (String, Int) -> Unit,
    private val onChatClick: (HistorialItem) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    private var listaHistorial = listOf<HistorialItem>()

    fun submitList(lista: List<HistorialItem>) {
        val diffCallback = HistorialDiffCallback(listaHistorial, lista)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listaHistorial = lista
        diffResult.dispatchUpdatesTo(this)
    }

    inner class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtCategoria: TextView = itemView.findViewById(R.id.txtCategoria)
        private val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        private val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnElminar)
        private val btnDetalles: ImageButton = itemView.findViewById(R.id.btnDetalles)
        private val btnReiniciar: ImageButton = itemView.findViewById(R.id.btnReiniciar)
        private val btnCalificar: ImageButton = itemView.findViewById(R.id.btnCalificar)
        private val btnChat: ImageButton = itemView.findViewById(R.id.btnChat)

        fun bind(item: HistorialItem) {
            txtNombre.text = item.nombreCliente
            txtCategoria.text = item.categoria
            txtDescripcion.text = item.descripcion
            txtFecha.text = item.fechaCreacion

            if (item.estado.equals("aceptado", ignoreCase = true) && item.nombreColaborador.isNotBlank()) {
                txtEstado.text = "Aceptado por ${item.nombreColaborador}"
                txtEstado.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark))
            } else {
                txtEstado.text = item.estado.replaceFirstChar { it.uppercaseChar() }
                txtEstado.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
            }
            btnEliminar.visibility = if (
                item.estado.equals("aceptado", ignoreCase = true) ||
                item.estado.equals("completado", ignoreCase = true) ||
                item.estado.equals("finalizado", ignoreCase = true) ||
                item.estado.equals("cancelado", ignoreCase = true)
            ) View.GONE else View.VISIBLE
            btnEliminar.setOnClickListener {
                onEliminarClick(item)
            }
            btnDetalles.visibility = when {
                item.estado.equals("aceptado", ignoreCase = true) && rolUsuario == "colaborador" -> View.VISIBLE
                else -> View.GONE
            }
            btnChat.visibility = when {
                item.estado.equals("aceptado", ignoreCase = true) -> View.VISIBLE
                else -> View.GONE
            }
            btnChat.setOnClickListener {
                onChatClick(item)
            }
            btnCalificar.visibility = when {
                item.estado.equals("finalizado", ignoreCase = true) && rolUsuario == "cliente" -> View.VISIBLE
                else -> View.GONE
            }
            btnCalificar.setOnClickListener {
                val context = itemView.context
                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calificacion, null)
                val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)

                AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton("Aceptar") { _, _ ->
                        val puntaje = ratingBar.rating.toInt()
                        val colaboradorId = item.colaboradorId
                        if (!colaboradorId.isNullOrBlank()) {
                            onCalificar(colaboradorId, puntaje)
                        } else {
                            Toast.makeText(context, "Colaborador no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            btnDetalles.setOnClickListener { view ->
                if (rolUsuario.equals("colaborador", ignoreCase = true) &&
                    item.estado.equals("aceptado", ignoreCase = true)) {

                    val popupMenu = PopupMenu(view.context, view)
                    popupMenu.menuInflater.inflate(R.menu.menu_opciones_solicitud, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_finalizar -> {
                                onActualizarEstado(item.id, "finalizado")
                                true
                            }
                            R.id.menu_cancelar -> {
                                onActualizarEstado(item.id, "cancelado")
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }
            }
            btnReiniciar.visibility = if (
                item.estado.equals("cancelado", ignoreCase = true) &&
                rolUsuario.equals("cliente", ignoreCase = true)
            ) {
                View.VISIBLE
            } else {
                View.GONE
            }
            btnReiniciar.setOnClickListener {
                AlertDialog.Builder(itemView.context)
                    .setTitle("¿Solicitar de nuevo?")
                    .setMessage("¿Quieres solicitar el mismo servicio?")
                    .setPositiveButton("Aceptar") { _, _ ->
                        onActualizarEstado(item.id, "pendiente")
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
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


