
package com.example.my_app_project.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_app_project.R
import com.example.my_app_project.databinding.FragmentNotificationsBinding
import com.example.my_app_project.domain.model.Notificacion
import com.example.my_app_project.ui.adapter.NotiAdapter

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var adapter: NotiAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        // Datos de ejemplo
        val lista = listOf(
            Notificacion(
                "1",
                "Confirmación de Reserva",
                "Tu cita con Juan T. (Electricista) ha sido confirmada para mañana a las 4:00 PM.",
                "Hoy",
                "2 mins ago",
                R.drawable.ic_persona,
                false
            ),
            Notificacion(
                "2",
                "Nuevo Servicio Disponible",
                "¡Ahora ofrecemos servicio de Mecánico! Encuentra al experto que necesitas.",
                "Ayer",
                "1 hour ago",
                R.drawable.ic_sistema,
                false
            ),
            Notificacion(
                "3",
                "Promoción Especial",
                "Este mes, disfruta de 15% de descuento en servicios de Carpintería.",
                "12/05/2025",
                "2 days ago",
                R.drawable.ic_sistema,
                false
            ),
            Notificacion(
                "4",
                "Califica tu Experiencia",
                "No olvides calificar tu servicio con Pedro Z. para ayudar a otros usuarios.",
                "Hoy",
                "5 mins ago",
                R.drawable.ic_star,
                true
            )
        )


        adapter = NotiAdapter(lista)
        binding.recyclerNotificaciones.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotificaciones.adapter = adapter

        return binding.root
    }
}
