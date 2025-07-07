package com.example.my_app_project.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentServiciosPostBinding
import com.example.my_app_project.domain.model.ServicioPost
import com.example.my_app_project.presentation.serviciosPost.ServicioPostViewModel
import com.example.my_app_project.ui.adapter.ServicioPostAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiciosPostFragment : Fragment() {
    @Inject lateinit var firebaseAuth: FirebaseAuth

    private var _binding: FragmentServiciosPostBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ServicioPostViewModel by viewModels()
    private lateinit var adapter: ServicioPostAdapter
    private var categoriaSeleccionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiciosPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recuperarCategoriaSeleccionada()
        inicializarRecyclerView()
        observarErrores()
        observarListaServicios()
    }

    private fun inicializarRecyclerView() {
        adapter = ServicioPostAdapter(
            auth = firebaseAuth,
            viewModel = viewModel,
            onLikeClicked = { postId, colaboradorUid ->
                viewModel.toggleLike(postId, colaboradorUid)
            },
            onCommentClick = { postId, colaboradorUid ->
                val bottomSheet = ComentariosBottomSheetFragment().apply {
                    arguments = Bundle().apply {
                        putString("postId", postId)
                        putString("colaboradorUid", colaboradorUid)
                    }
                }
                bottomSheet.show(parentFragmentManager, "ComentariosBottomSheet")
            },
            onShareClick = { servicioPost ->
                viewModel.incrementarContadorShares(servicioPost.id, servicioPost.uidColaborador)
                compartirPost(servicioPost)
            }
        )

        binding.recyclerServiciosPost.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerServiciosPost.adapter = adapter
    }

    private fun recuperarCategoriaSeleccionada() {
        categoriaSeleccionada = arguments?.getString("categoria_seleccionada")
    }

    private fun observarErrores() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.error.collect { mensajeError ->
                    mensajeError?.let {
                        Log.e("ServiciosPostFragment", "Error: $it")
                    }
                }
            }
        }
    }

    private fun observarListaServicios() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serviciosPost.collect { listaCompleta ->
                    categoriaSeleccionada?.let { categoria ->
                        val listaFiltrada = listaCompleta.filter {
                            it.categoriaServicioPost.equals(categoria, ignoreCase = true)
                        }
                        adapter.submitList(listaFiltrada)
                    }
                }
            }
        }
    }
    private fun compartirPost(servicioPost: ServicioPost) {
        val textoCompartir = """
        Mira este servicio en nuestra app:
        🔧 ${servicioPost.categoriaServicioPost}
        👤 ${servicioPost.nombreUsuarioServicioPost}
        💬 ${servicioPost.descripcionServicioPost}
        💰 S/ ${servicioPost.tarifaServicioPost}.00
        
        Descarga la app para más info.
    """.trimIndent()

        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textoCompartir)
            type = "text/plain"
        }

        val chooser = Intent.createChooser(intent, "Compartir servicio con...")
        startActivity(chooser)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
