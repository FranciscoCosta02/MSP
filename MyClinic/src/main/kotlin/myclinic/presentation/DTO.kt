package myclinic.presentation

import io.swagger.v3.oas.annotations.media.Schema
import myclinic.data.AppointmentRegime
import myclinic.data.ScheduleState
import myclinic.data.ScheduleType
import java.time.LocalDate

/**
 * Users
 */
open class UserDTO(open val username: String, open val name: String, open val email: String, open val password: String,
                   open val phone: String)

data class ClientDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String, val nif: String,
                     val medicalHistoryDTO: MedicalHistoryDTO, val householdDTO: HouseholdDTO)
    : UserDTO(username, name, email, password, phone)

data class DoctorDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String)
    : UserDTO(username, name, email, password, phone)

/**
 * Short Users
 */
open class UserShortDTO(open val username: String, open val email: String, open val phone: String)

data class ClientShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

data class DoctorShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

/**
 * All Schedule
 */
open class ScheduleDTO(open val id: Long, open val client: ClientShortDTO, open val doctor: String,
                       open var state: ScheduleState, open val date: LocalDate, open val type: ScheduleType)

data class AppointmentDTO(override val id: Long, override val client: ClientShortDTO,
                          override val doctor: String, override var state: ScheduleState,
                          override val date: LocalDate, val regime: AppointmentRegime)
    : ScheduleDTO(id, client, doctor, state, date, ScheduleType.APPOINTMENT)

data class AddAppointmentDTO(val clientUsername: String, val doctorUsername: String, val date: LocalDate,
                             val regime: AppointmentRegime, val type: ScheduleType = ScheduleType.APPOINTMENT)

data class ExamDTO(override val id: Long, override val client: ClientShortDTO, override val doctor: String,
                   override var state: ScheduleState, override val date: LocalDate, val equipment: String)
    : ScheduleDTO(id, client, doctor, state, date, ScheduleType.EXAM)

data class AddExamDTO(val clientUsername: String, val doctorUsername: String, val date: LocalDate,
                      val equipment: String, val type: ScheduleType = ScheduleType.EXAM)

/**
 * Household
 */
data class HouseholdDTO(val id: Long, val clients: List<ClientShortDTO>)

/**
 * Medical History
 */
data class MedicalHistoryDTO(val id: Long, val clientUsername: String, val doctorUsernames: List<String>,
                             val appointments: List<AppointmentDTO>, val exams: List<ExamDTO>,
                             val prescriptions: List<PrescriptionDTO>)

data class AddMedicalHistoryDTO(val clientUsername: String, val doctorUsernames: List<String>,
                             val appointments: List<AppointmentDTO>, val exams: List<ExamDTO>,
                             val prescriptions: List<PrescriptionDTO>)

/**
 * Prescriptions
 */
data class PrescriptionDTO(val id: Long, val content: String, val clientUsername: String, val doctorUsername: String)

data class AddPrescriptionDTO(val content: String, val clientUsername: String, val doctorUsername: String)