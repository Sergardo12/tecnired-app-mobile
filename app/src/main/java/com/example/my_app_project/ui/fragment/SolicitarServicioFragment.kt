
package com.example.my_app_project.ui.fragment

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.my_app_project.BuildConfig
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentSolicitarServicioBinding
import com.example.my_app_project.domain.model.ServicioSolicitud
import com.example.my_app_project.presentation.autenticacion.AuthViewModel
import com.example.my_app_project.presentation.categorias.CategoriaViewModel
import com.example.my_app_project.presentation.notificaciones.NotificacionesViewModel
import com.example.my_app_project.presentation.servicioSolicitud.ServicioSolicitudViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SolicitarServicioFragment : Fragment(), OnMapReadyCallback {

    // ViewBinding
    private var _binding: FragmentSolicitarServicioBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private val solicitudViewModel: ServicioSolicitudViewModel by viewModels()
    private val categoriaViewModel: CategoriaViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModel: NotificacionesViewModel by viewModels()


    // Mapa
    private lateinit var mapa: GoogleMap
    private lateinit var mapaFragmento: SupportMapFragment

    // Estado interno
    private var categoriaIdSeleccionada: String? = null


    // Inicialización del Fragmento
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         //Inicializar Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.GOOGLE_MAPS_KEY, Locale.getDefault())
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSolicitarServicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Cuando la vista ya está creada
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Mapa
        mapaFragmento = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().replace(R.id.mapContainer, mapaFragmento).commit()
        mapaFragmento.getMapAsync(this)

        // Inicializaciones
        configurarAutocompletadoDireccion(view)
        authViewModel.obtenerUsuarioActual()
        observarCategorias(view)
        observarUsuarioActual()
        observarResultadoSolicitud()

        // Botón para enviar
        binding.btnSolicitarServicio.setOnClickListener { enviarSolicitud() }
    }

    // Callback cuando el mapa esté listo
    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap

        val lima = LatLng(-12.0464, -77.0428)
        mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 14f))
        mapa.uiSettings.isZoomControlsEnabled = true

        // Permisos de ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            mapa.isMyLocationEnabled = true
            mapa.uiSettings.isMyLocationButtonEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }

        // Clic en el mapa para seleccionar dirección
        mapa.setOnMapClickListener { latLng ->
            mapa.clear()
            mapa.addMarker(MarkerOptions().position(latLng).title("Ubicación seleccionada"))
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                binding.inputDireccion.setText(addresses[0].getAddressLine(0))
            }
        }
    }

    // Solicitud de permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mapa.isMyLocationEnabled = true
                mapa.uiSettings.isMyLocationButtonEnabled = true
            }
        }
    }

    // Dirección con autocompletado de Google Places
    private fun configurarAutocompletadoDireccion(view: View) {
        val inputDireccion = view.findViewById<MaterialAutoCompleteTextView>(R.id.inputDireccion)
        val placesClient = Places.createClient(requireContext())
        val token = AutocompleteSessionToken.newInstance()

        inputDireccion.doOnTextChanged { text, _, _, _ ->
            if (text != null && text.length >= 3) {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(text.toString())
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        val suggestions = response.autocompletePredictions.map {
                            it.getFullText(null).toString()
                        }
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions)
                        inputDireccion.setAdapter(adapter)
                        inputDireccion.showDropDown()
                        inputDireccion.setOnItemClickListener { parent, _, position, _ ->
                            val selectedAddress = parent.getItemAtPosition(position).toString()
                            val geocoder = Geocoder(requireContext(), Locale.getDefault())
                            val addresses = geocoder.getFromLocationName(selectedAddress, 1)
                            if (!addresses.isNullOrEmpty()) {
                                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                                mapa.clear()
                                mapa.addMarker(MarkerOptions().position(location).title(selectedAddress))
                                mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                            }
                        }
                    }
                    .addOnFailureListener { it.printStackTrace() }
            }
        }
    }

    // Observar categorías desde el ViewModel
    private fun observarCategorias(view: View) {
        val inputCategoria = view.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteCategoria)
        categoriaViewModel.nombresCategorias.onEach { categorias ->
            val nombres = categorias.map { it.nombreCategoria }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres)
            inputCategoria.setAdapter(adapter)

            inputCategoria.setOnItemClickListener { parent, _, position, _ ->
                val nombre = parent.getItemAtPosition(position).toString()
                viewLifecycleOwner.lifecycleScope.launch {
                    val categoria = categoriaViewModel.obtenerTarifasDeCategorias(nombre)

                    println("DEBUG -> Categoría seleccionada: $nombre")
                    println("DEBUG -> ID: ${categoria?.id}, Min: ${categoria?.tarifaMinCategoria}, Max: ${categoria?.tarifaMaxCategoria}")

                    categoriaIdSeleccionada = categoria?.id

                    if (categoria != null && categoria.tarifaMinCategoria > 0.00 && categoria.tarifaMaxCategoria > 0.00) {
                        val mensaje = "💬 Precio estimado: S/ ${categoria.tarifaMinCategoria} - S/ ${categoria.tarifaMaxCategoria}"
                        binding.textTarifaEstimado.text = mensaje
                        binding.textTarifaEstimado.visibility = View.VISIBLE
                    } else {
                        binding.textTarifaEstimado.text = "No hay tarifa definida"
                        binding.textTarifaEstimado.visibility = View.VISIBLE
                    }
                }

            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // Enviar la solicitud al ViewModel
    private fun enviarSolicitud() {
        val direccion = binding.inputDireccion.text.toString().trim()
        val descripcion = binding.inputDescripcionServicio.text.toString().trim()
        val categoriaId = categoriaIdSeleccionada
        val usuario = authViewModel.usuarioActual.value


        val latLng = try {
            mapa.cameraPosition.target
        } catch (e: Exception) {
            LatLng(-12.0464, -77.0428) // Fallback
        }

        if (direccion.isNotEmpty() && descripcion.isNotEmpty() && categoriaId != null && usuario != null) {
            val solicitud = ServicioSolicitud(
                id = "",
                clienteId = usuario.uid,
                categoriaId = categoriaId,
                direccion = direccion,
                descripcion = descripcion,
                latitud = latLng.latitude,
                longitud = latLng.longitude,
            )
            solicitudViewModel.crearSolicitud(solicitud)
        } else {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    // Observar el estado del resultado
    private fun observarResultadoSolicitud() {
        solicitudViewModel.estadoSolicitud.onEach { resultado ->
            resultado?.onSuccess {
                Toast.makeText(requireContext(), "Solicitud enviada con éxito", Toast.LENGTH_SHORT).show()
                viewModel.crearNotificacionesDesdeServiciosPendientes()
                solicitudViewModel.limpiarEstado()
                limpiarCampos()
            }?.onFailure {
                Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_LONG).show()
                solicitudViewModel.limpiarEstado()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // Habilitar botón si el usuario está logueado
    private fun observarUsuarioActual() {
        authViewModel.usuarioActual.onEach { usuario ->
            binding.btnSolicitarServicio.isEnabled = usuario != null
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // Limpiar los campos del formulario
    private fun limpiarCampos() {
        binding.inputDireccion.setText("")
        binding.inputDescripcionServicio.setText("")
        binding.autoCompleteCategoria.setText("")
        categoriaIdSeleccionada = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SolicitarServicioFragment().apply {
                // Agrega argumentos si es necesario
            }
    }
}
