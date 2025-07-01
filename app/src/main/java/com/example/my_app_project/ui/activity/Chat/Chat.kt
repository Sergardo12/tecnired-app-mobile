package com.example.my_app_project.ui.activity.Chat

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.R
import com.example.my_app_project.databinding.ActivityChatBinding
import com.example.my_app_project.presentation.Chat.ChatViewModel
import com.example.my_app_project.ui.adapter.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Chat : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var servicioId: String
    private lateinit var uidActual: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        servicioId = intent.getStringExtra("servicioId") ?: return
        uidActual = FirebaseAuth.getInstance().currentUser?.uid ?: return

        configurarRecyclerView()

        chatViewModel.escucharMensajes(servicioId)

        chatViewModel.mensajes.observe(this) { mensajes ->
            chatAdapter.submitList(mensajes)
            binding.recyclerMensajes.scrollToPosition(mensajes.size - 1)
        }

        binding.btnEnviar.setOnClickListener {
            val contenido = binding.etMensaje.text.toString().trim()
            if (contenido.isNotEmpty()) {
                chatViewModel.enviarMensaje(servicioId, contenido)
                binding.etMensaje.setText("")
            }
        }

        binding.btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun configurarRecyclerView() {
        chatAdapter = ChatAdapter(uidActual)
        binding.recyclerMensajes.layoutManager = LinearLayoutManager(this)
        binding.recyclerMensajes.adapter = chatAdapter
    }
}