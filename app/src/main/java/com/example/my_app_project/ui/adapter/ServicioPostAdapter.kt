package com.example.my_app_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.my_app_project.R
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.presentation.serviciosPost.ServicioPostViewModel
import com.google.firebase.auth.FirebaseAuth

import javax.inject.Inject

/**
 * Adaptador para mostrar una lista de publicaciones de servicios (ServicioPost) en un RecyclerView.
 */
class ServicioPostAdapter @Inject constructor(
    private val auth: FirebaseAuth,
    private val viewModel: ServicioPostViewModel,
    private val onLikeClicked: (postId: String, colaboradorUid: String) -> Unit,
    private val onCommentClick: (postId: String, colaboradorUid: String) -> Unit,
    private val onShareClick: (ServicioPost) -> Unit

) : ListAdapter<ServicioPost, ServicioPostAdapter.ServicioPostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicioPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_servicio_post, parent, false)
        return ServicioPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicioPostViewHolder, position: Int) {
        val servicioPost = getItem(position)
        holder.bind(servicioPost)

        val usuarioUid = auth.currentUser?.uid
        val postTieneLike = servicioPost.likes.contains(usuarioUid)

        val btnLike = holder.itemView.findViewById<ImageView>(R.id.btnLike)
        btnLike.setImageResource(
            if (postTieneLike) R.drawable.ic_like_filled else R.drawable.ic_like
        )
        btnLike.setOnClickListener {
            onLikeClicked(servicioPost.id, servicioPost.uidColaborador)
        }
        val btnComment = holder.itemView.findViewById<ImageView>(R.id.btnComment)
        btnComment.setOnClickListener {
            onCommentClick(servicioPost.id, servicioPost.uidColaborador)
        }

        val btnShare = holder.itemView.findViewById<ImageView>(R.id.btnShare)
        btnShare.setOnClickListener {
            onShareClick(servicioPost)
        }
        viewModel.cargarComentarioPreview(servicioPost.id, servicioPost.uidColaborador) { comentario ->
            if (comentario != null) {
                holder.layoutComentarioPreview.visibility = View.VISIBLE
                holder.tvNombreUsuarioComentarioPreview.text = comentario.nombreUsuario
                holder.tvContenidoComentarioPreview.text = comentario.contenido
            } else {
                holder.layoutComentarioPreview.visibility = View.GONE
            }
        }



    }

    class ServicioPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagen: ImageView = itemView.findViewById(R.id.imgServicio)
        private val nombreUsuario: TextView = itemView.findViewById(R.id.tvNombreUsuario)
        private val descripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val categoria: TextView = itemView.findViewById(R.id.categoriaServicioPost)
        private val tarifa: TextView = itemView.findViewById(R.id.tvTarifa)
        private val likeCount: TextView = itemView.findViewById(R.id.tvLikeCount)
        private val commentCount: TextView = itemView.findViewById(R.id.tvCommentCount)
        private val shareCount: TextView = itemView.findViewById(R.id.tvShareCount)

        val layoutComentarioPreview: LinearLayout= itemView.findViewById(R.id.layoutComentarioPreview)
        val tvNombreUsuarioComentarioPreview: TextView = itemView.findViewById(R.id.tvNombreUsuarioComentarioPreview)
        val tvContenidoComentarioPreview: TextView = itemView.findViewById(R.id.tvContenidoComentarioPreview)




        fun bind(servicio: ServicioPost) {
            Glide.with(itemView.context)
                .load(servicio.imagenServicioPost)
                .into(imagen)

            val nombreCompleto = servicio.nombreUsuarioServicioPost
            nombreUsuario.text = nombreCompleto
            descripcion.text = servicio.descripcionServicioPost
            categoria.text = servicio.categoriaServicioPost
            tarifa.text = "S/ ${servicio.tarifaServicioPost}.00"
            likeCount.text = servicio.likes.size.toString()
            commentCount.text = servicio.commentCount.toString()
            shareCount.text = servicio.shareCount.toString()

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ServicioPost>() {
        override fun areItemsTheSame(oldItem: ServicioPost, newItem: ServicioPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ServicioPost, newItem: ServicioPost): Boolean {
            return oldItem == newItem
        }
    }

}
