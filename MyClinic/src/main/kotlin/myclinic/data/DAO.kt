package myclinic.data

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class ClientDAO(@Id val username: String, val name: String, val email: String, val password: String,
                     val phone: String, val nif: String, @OneToOne val medicalHistory: MedicalHistoryDAO,
                     @ManyToOne val household: HouseholdDAO)

@Entity
data class HouseholdDAO(@Id @GeneratedValue val id: Long,
                        @OneToMany(mappedBy = "household") val clients: MutableList<ClientDAO>)

@Entity
data class MedicalHistoryDAO(@Id @GeneratedValue val id: Long, @OneToOne var client: ClientDAO?,
                             @OneToMany(mappedBy = "medicalHistory") val appointments: MutableList<AppointmentDAO>,
                             @OneToMany(mappedBy = "medicalHistory") val exams: MutableList<ExamDAO>,
                             @OneToMany(mappedBy = "medicalHistory") val prescriptions: MutableList<PrescriptionDAO>)

@Entity
data class DoctorDAO(@Id val username: String, val name: String, val email: String, val password: String,
                     val phone: String, val specialty: Speciality)

@Entity
data class AppointmentDAO(@Id @GeneratedValue val id: Long, val date: LocalDate, val regime: AppointmentRegime,
                          val scheduleType: ScheduleType = ScheduleType.APPOINTMENT, var state: ScheduleState,
                          @ManyToOne val client: ClientDAO, val doctor: String,
                          @ManyToOne val medicalHistory: MedicalHistoryDAO, val type: Speciality)

@Entity
data class ExamDAO(@Id @GeneratedValue val id: Long, val date: LocalDate, val equipment: String,
                   val scheduleType: ScheduleType = ScheduleType.EXAM, var state: ScheduleState,
                   @ManyToOne val client: ClientDAO, val doctor: String,
                   @ManyToOne val medicalHistory: MedicalHistoryDAO, val type: Speciality)

@Entity
data class PrescriptionDAO(@Id @GeneratedValue val id: Long, val content: String,
                           @ManyToOne val client: ClientDAO, val doctor: String,
                           @ManyToOne val medicalHistory: MedicalHistoryDAO)

enum class AppointmentRegime(val type: String) {
    IN_PERSON("in person"),
    ONLINE("online")
}

enum class ScheduleState(val state: String) {
    SCHEDULED("scheduled"),
    READY("ready"),
    COMPLETED("completed")
}

enum class ScheduleType(val type: String) {
    APPOINTMENT("appointment"),
    EXAM("exam")
}

enum class Speciality(val type: String) {
    GEN_FAM("general practice family medicine"),
    PEDI("pediatrics"),
    GYNE_OBST("gynecology obstetrics"),
    CARDIO("cardiology"),
    ORTHO("orthopedics"),
    DERMA("dermatology"),
    PSYCH("psychiatry"),
    OPHTHAL("ophthalmology"),
    ENT("otolaryngologies"),
    GASTRO("gastroenterology")
}