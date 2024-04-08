package myclinic.application

import myclinic.data.AppointmentDAO
import myclinic.data.AppointmentRepository

class AppointmentApplication(
    val appointments: AppointmentRepository
) {

    fun addAppointment(appointment: AppointmentDAO) {
        appointments.save(appointment)
    }

    fun getAllClientAppointments(username: String): Iterable<AppointmentDAO> =
        appointments.findAllByClientUsername(username)

}