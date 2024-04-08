package myclinic.data

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class ClientDAO(@Id val username: String, val name: String, val email: String, val password: String, val phone: String,
                     val nif: String, @OneToOne val medicalHistory: MedicalHistoryDAO, @ManyToOne val household: HouseholdDAO)

@Entity
data class HouseholdDAO(@Id @GeneratedValue val id: Long, @OneToMany(mappedBy = "household") val clients: MutableList<ClientDAO>)

@Entity
data class MedicalHistoryDAO(@Id @GeneratedValue val id: Long, @OneToOne var client: ClientDAO?, @OneToOne var doctor: DoctorDAO?,
                             @OneToMany(mappedBy = "medicalHistory") val appointments: MutableList<AppointmentDAO>,
                             @OneToMany(mappedBy = "medicalHistory") val exams: MutableList<ExamDAO>,
                             @OneToMany(mappedBy = "medicalHistory") val prescriptions: MutableList<PrescriptionDAO>)

@Entity
data class DoctorDAO(@Id val username: String, val name: String, val email: String, val password: String, val phone: String,
                     @OneToOne val medicalHistory: MedicalHistoryDAO)

@Entity
data class AppointmentDAO(@Id @GeneratedValue val id: Long, val date: LocalDate, val type: AppointmentType,
                          var state: ScheduleState, @ManyToOne val client: ClientDAO, @ManyToOne val doctor: DoctorDAO)

@Entity
data class ExamDAO(@Id @GeneratedValue val id: Long, val date: LocalDate, val equipment: String,
                   var state: ScheduleState, @ManyToOne val client: ClientDAO, @ManyToOne val doctor: DoctorDAO)

@Entity
data class PrescriptionDAO(@Id @GeneratedValue val id: Long, val content: String,
                           @ManyToOne val client: ClientDAO, @ManyToOne val doctor: DoctorDAO)

enum class AppointmentType(val type: String) {
    IN_PERSON("in person"),
    ONLINE("online")
}

enum class ScheduleState(val state: String) {
    SCHEDULED("scheduled"),
    READY("ready"),
    COMPLETED("completed")
}