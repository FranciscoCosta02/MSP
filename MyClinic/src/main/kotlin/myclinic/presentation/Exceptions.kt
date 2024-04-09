package myclinic.presentation

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(message: String): Exception(message)

enum class Constants(val message: String) {
    CLIENT_NOT_FOUND("Client not found"),
    DOCTOR_NOT_FOUND("Doctor not found"),
    APPOINTMENT_NOT_FOUND("Appointment not found"),
    EXAM_NOT_FOUND("Exam not found")
}