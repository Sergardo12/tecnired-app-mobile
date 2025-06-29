package com.example.my_app_project.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.databinding.FragmentSolicitudesColaboradorBinding
import com.example.my_app_project.presentation.servicioSolicitud.ServicioSolicitudViewModel
import com.example.my_app_project.ui.adapter.SolicitudColaboradorAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SolicitudesColaboradorFragment : Fragment() {

    private val viewModel: ServicioSolicitudViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentSolicitudesColaboradorBinding
    private lateinit var adapter: SolicitudColaboradorAdapter

    // Nuevo launcher para el permiso
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                obtenerUbicacion()
            } else {
                Toast.makeText(
                    context,
                    "Permiso de ubicación denegado. No se pueden cargar solicitudes.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolicitudesColaboradorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SolicitudColaboradorAdapter(emptyList())
        binding.recyclerSolicitudesColaborador.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSolicitudesColaborador.adapter = adapter



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        configurarFiltros()
        verificarPermisosUbicacion()
        observarSolicitudes()

        binding.btnActualizarSolicitudes.setOnClickListener {
            actualizarSolicitudesConUbicacion()
        }
    }

    private fun configurarFiltros() {
        binding.seekBarDistancia.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                binding.textDistanciaSeleccionada.text = "$progress km"
                actualizarSolicitudesConUbicacion()
            }

            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.switchOrden.setOnCheckedChangeListener { _, _ ->
            actualizarSolicitudesConUbicacion()
        }
    }

    private fun actualizarSolicitudesConUbicacion() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@addOnSuccessListener
                    val distancia = binding.seekBarDistancia.progress.toDouble()
                    val ascendente = binding.switchOrden.isChecked

                    viewModel.obtenerSolicitudesFiltradas(
                        uid = uid,
                        lat = location.latitude,
                        lon = location.longitude,
                        distanciaMaxKm = distancia,
                        ascendente = ascendente
                    )
                }
            }
        }
    }


    private fun verificarPermisosUbicacion() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                obtenerUbicacion()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(
                    context,
                    "Se necesita permiso de ubicación para mostrar solicitudes cercanas.",
                    Toast.LENGTH_LONG
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun obtenerUbicacion() {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    cargarSolicitudes(location.latitude, location.longitude)
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Error de seguridad al obtener ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cargarSolicitudes(latitud: Double, longitud: Double) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.obtenerSolicitudesFiltradas(
            uid = uid,
            lat = latitud,
            lon = longitud,
            distanciaMaxKm = 15.0,
            ascendente = true
        )
    }

    private fun observarSolicitudes() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.solicitudesConDistancia.collectLatest { lista ->
                adapter.actualizarLista(lista)
                binding.tvListaVacia.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }
}
