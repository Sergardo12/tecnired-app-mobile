package com.example.my_app_project.ui.fragment

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentHomeBinding
import com.example.my_app_project.domain.model.Categoria
import com.example.my_app_project.presentation.categorias.CategoriaViewModel
import com.example.my_app_project.ui.adapter.CategoriasAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.log

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragment", "onCreate ejecutado")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment ", "onViewCreated ejecutado")

        childFragmentManager.beginTransaction()
            .replace(R.id.contenedor_categoria_fragment, CategoryFragment())
            .commit()

        childFragmentManager.beginTransaction()
            .replace(R.id.contenedor_mejores_colaboradores_fragment, MejorColaboradorFragment())
            .commit()

        // Abrir el bootmdialog
        binding.cardCrearPublicacion.setOnClickListener {
            val bottomSheet = CrearPostBottomSheet()
            bottomSheet.show(parentFragmentManager, CrearPostBottomSheet::class.java.simpleName)
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {

            }
    }
}