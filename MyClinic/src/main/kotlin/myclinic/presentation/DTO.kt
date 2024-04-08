package myclinic.presentation

import io.swagger.v3.oas.annotations.media.Schema
import myclinic.data.AppointmentType
import myclinic.data.ScheduleState
import org.springframework.security.core.userdetails.User
import java.time.LocalDate

/**
 * Users
 */
open class UserDTO(open val username: String, open val name: String, open val email: String, open val password: String,
                   open val phone: String, open val medicalHistoryDTO: MedicalHistoryDTO)

data class AddClientDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String, val nif: String,
                     override val medicalHistoryDTO: MedicalHistoryDTO, val householdDTO: HouseholdDTO)
    : UserDTO(username, name, email, password, phone, medicalHistoryDTO)

data class AddDoctorDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String,
                     override val medicalHistoryDTO: MedicalHistoryDTO)
    : UserDTO(username, name, email, password, phone, medicalHistoryDTO)

/**
 * Short Users
 */
@Schema(name = "Short User", description = "Short version of user")
open class UserShortDTO(open val username: String, open val email: String, open val phone: String)

@Schema(name= "Short Client", description = "Short version of client")
data class ClientShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

@Schema(name= "Short Doctor", description = "Short version of doctor")
data class DoctorShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

/**
 * All Schedule
 */
open class ScheduleDTO(open val id: Long, open val clientUsername: String, open val doctorUsername: String,
                       open var state: ScheduleState, open val date: LocalDate)

@Schema(name= "Appointment", description = "Appointment associated to a client and doctor")
data class AppointmentDTO(override val id: Long, override val clientUsername: String,
                          override val doctorUsername: String, override var state: ScheduleState,
                          override val date: LocalDate, val type: AppointmentType)
    : ScheduleDTO(id, clientUsername, doctorUsername, state, date)

@Schema(name= "Appointment to add", description = "Appointment to register")
data class AddAppointmentDTO(val clientUsername: String, val doctorUsername: String,
                             val date: LocalDate, val type: AppointmentType)

data class ExamDTO(override val id: Long, override val clientUsername: String, override val doctorUsername: String,
                   override var state: ScheduleState, override val date: LocalDate, val equipment: String)
    : ScheduleDTO(id, clientUsername, doctorUsername, state, date)

data class AddExamDTO(val clientUsername: String, val doctorUsername: String,
                             val date: LocalDate, val equipment: String)

/**
 * Household
 */
data class HouseholdDTO(val id: Long, val clients: List<ClientShortDTO>)

/**
 * Medical History
 */
data class MedicalHistoryDTO(val id: Long, var clientUsername: String?, var doctorUsername: String?,
                             val appointments: List<AppointmentDTO>, val exams: List<ExamDTO>,
                             val prescriptions: List<PrescriptionDTO>)

/**
 * Prescriptions
 */
data class PrescriptionDTO(val id: Long, val content: String, val clientUsername: String, val doctorUsername: String)

data class AddPrescriptionDTO(val content: String, val clientUsername: String, val doctorUsername: String)