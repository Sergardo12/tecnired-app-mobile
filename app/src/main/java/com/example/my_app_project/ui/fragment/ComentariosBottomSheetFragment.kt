package com.example.my_app_project.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentComentariosBottomSheetBinding
import com.example.my_app_project.presentation.comentario.ComentarioViewModel
import com.example.my_app_project.ui.adapter.ComentarioAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ComentariosBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentComentariosBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ComentarioViewModel by viewModels()

    private lateinit var adapter: ComentarioAdapter
    private lateinit var postId: String
    private lateinit var colaboradorUid: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComentariosBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postId = requireArguments().getString("postId") ?: return
        colaboradorUid = requireArguments().getString("colaboradorUid") ?: return

        adapter = ComentarioAdapter()
        binding.recyclerComentarios.adapter = adapter

        observarComentarios()
        configurarEnviarComentario()
        observarComentariosEnviados()



    }

    private fun observarComentarios() {
        viewModel.obtenerComentarios(postId, colaboradorUid)
        lifecycleScope.launch {
            viewModel.comentarios.collect {
                adapter.submitList(it)
            }
        }
    }

    private fun configurarEnviarComentario() {
        binding.btnEnviarComentario.setOnClickListener {
            val texto = binding.etComentario.text.toString()
            if (texto.isNotBlank()) {
                viewModel.enviarComentario(postId, colaboradorUid, texto)
                binding.etComentario.setText("")

            }
        }
    }

    private fun observarComentariosEnviados(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.comentarioEnviadoExitosamente.collect { enviado ->
                    if (enviado) dismiss()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}