
package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentNotificationsBinding
import com.example.my_app_project.domain.model.Notificacion
import com.example.my_app_project.presentation.notificaciones.NotificacionesViewModel
import com.example.my_app_project.ui.adapter.NotiAdapter
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NotificacionesViewModel by viewModels()
    private lateinit var adapter: NotiAdapter
    private val uid: String? get() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        adapter = NotiAdapter(emptyList(), ::onNotificacionClick)

        binding.recyclerNotificaciones.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotificaciones.adapter = adapter

        viewModel.notificaciones.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
            binding.textNoNotificaciones.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
        }

        uid?.let {
            viewModel.cargar(it)
        }


        binding.btnTodos.setOnClickListener {
            viewModel.filtrarTodos()
        }

        binding.btnNoLeidos.setOnClickListener {
            viewModel.filtrarNoLeidos()
        }

        binding.btnHoy.setOnClickListener {
            viewModel.filtrarHoy()
        }

        binding.edtBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.buscar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onNotificacionClick(noti: Notificacion) {
        if (!noti.leido) {
            uid?.let { userId ->
                viewModel.marcarLeida(userId, noti.id) {
                    Toast.makeText(requireContext(), "Notificación marcada como leída", Toast.LENGTH_SHORT).show()
                    viewModel.cargar(userId)
                }
            }
        }
    }

}

