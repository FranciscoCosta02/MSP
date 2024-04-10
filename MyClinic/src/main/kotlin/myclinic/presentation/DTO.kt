package myclinic.presentation

import io.swagger.v3.oas.annotations.media.Schema
import myclinic.data.AppointmentRegime
import myclinic.data.ScheduleState
import myclinic.data.ScheduleType
import java.time.LocalDate

/**
 * Users
 */
@Schema(name = "User", description = "Original version of user")
open class UserDTO(open val username: String, open val name: String, open val email: String, open val password: String,
                   open val phone: String, open val medicalHistoryDTO: MedicalHistoryDTO)

@Schema(name = "Client", description = "Original version of client")
data class ClientDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String, val nif: String,
                     override val medicalHistoryDTO: MedicalHistoryDTO, val householdDTO: HouseholdDTO)
    : UserDTO(username, name, email, password, phone, medicalHistoryDTO)

@Schema(name = "Doctor", description = "Original version of doctor")
data class DoctorDTO(override val username: String, override val name: String, override val email: String,
                     override val password: String, override val phone: String,
                     override val medicalHistoryDTO: MedicalHistoryDTO)
    : UserDTO(username, name, email, password, phone, medicalHistoryDTO)

/**
 * Short Users
 */
@Schema(name = "Short User", description = "Short version of user")
open class UserShortDTO(open val username: String, open val email: String, open val phone: String)

@Schema(name = "Short Client", description = "Short version of client")
data class ClientShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

@Schema(name = "Short Doctor", description = "Short version of doctor")
data class DoctorShortDTO(override val username: String, override val email: String, override val phone: String)
    : UserShortDTO(username, email, phone)

/**
 * All Schedule
 */
@Schema(name = "Schedule", description = "Original version of schedule")
open class ScheduleDTO(open val id: Long, open val client: ClientShortDTO, open val doctor: DoctorShortDTO,
                       open var state: ScheduleState, open val date: LocalDate, open val type: ScheduleType)

@Schema(name= "Appointment", description = "Appointment associated to a client and doctor")
data class AppointmentDTO(override val id: Long, override val client: ClientShortDTO,
                          override val doctor: DoctorShortDTO, override var state: ScheduleState,
                          override val date: LocalDate, val regime: AppointmentRegime,
                          override val type: ScheduleType = ScheduleType.APPOINTMENT)
    : ScheduleDTO(id, client, doctor, state, date, type)

@Schema(name= "Appointment to add", description = "Appointment to register")
data class AddAppointmentDTO(val clientUsername: String, val doctorUsername: String, val date: LocalDate,
                             val regime: AppointmentRegime, val type: ScheduleType = ScheduleType.APPOINTMENT)

@Schema(name= "Exam", description = "Exam associated to a client and doctor")
data class ExamDTO(override val id: Long, override val client: ClientShortDTO, override val doctor: DoctorShortDTO,
                   override var state: ScheduleState, override val date: LocalDate, val equipment: String,
                   override val type: ScheduleType = ScheduleType.EXAM)
    : ScheduleDTO(id, client, doctor, state, date, type)

@Schema(name= "Exam to add", description = "Exam to register")
data class AddExamDTO(val clientUsername: String, val doctorUsername: String, val date: LocalDate,
                      val equipment: String, val type: ScheduleType = ScheduleType.EXAM)

/**
 * Household
 */
@Schema(name = "Household", description = "Original version of household")
data class HouseholdDTO(val id: Long, val clients: List<ClientShortDTO>)

/**
 * Medical History
 */
@Schema(name = "Medical History", description = "Original version of medical history")
data class MedicalHistoryDTO(val id: Long, val clientUsername: String, val doctorUsernames: List<String>,
                             val appointments: List<AppointmentDTO>, val exams: List<ExamDTO>,
                             val prescriptions: List<PrescriptionDTO>)

@Schema(name = "Medical History to add", description = "Medical history to register")
data class AddMedicalHistoryDTO(val clientUsername: String, val doctorUsernames: List<String>,
                             val appointments: List<AppointmentDTO>, val exams: List<ExamDTO>,
                             val prescriptions: List<PrescriptionDTO>)

/**
 * Prescriptions
 */
@Schema(name = "Prescription", description = "Original version of prescription")
data class PrescriptionDTO(val id: Long, val content: String, val clientUsername: String, val doctorUsername: String)

@Schema(name = "Prescription to add", description = "Prescription to register")
data class AddPrescriptionDTO(val content: String, val clientUsername: String, val doctorUsername: String)