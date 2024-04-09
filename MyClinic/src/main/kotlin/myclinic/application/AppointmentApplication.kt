package myclinic.application

import myclinic.data.AppointmentDAO
import myclinic.data.AppointmentRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class AppointmentApplication(
    private val appointments: AppointmentRepository
) {

    fun addAppointment(appointment: AppointmentDAO) {
        appointments.save(appointment)
    }

    fun getAllClientAppointments(username: String): Iterable<AppointmentDAO> =
        appointments.findAllByClientUsername(username)

    fun getClientAppointment(username: String, id: Long): Optional<AppointmentDAO> =
        appointments.findByClientUsernameAndId(username, id)

}