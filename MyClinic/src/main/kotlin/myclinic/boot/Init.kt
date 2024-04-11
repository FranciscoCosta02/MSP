package myclinic.boot

import myclinic.data.*
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
@Order(1)
class Init (val clients: ClientRepository, val doctors: DoctorRepository,
            val medicalHistories: MedicalHistoryRepository, val households: HouseholdRepository,
            val appointments: AppointmentRepository, val exams: ExamRepository,
            val prescriptions: PrescriptionRepository): CommandLineRunner {
    override fun run(vararg args: String?) {

        /**
         * Client
         */
        val medicalHistoryClient = MedicalHistoryDAO(0, null, mutableListOf(), mutableListOf(), mutableListOf())
        medicalHistories.save(medicalHistoryClient)

        val household = HouseholdDAO(0, mutableListOf())
        households.save(household)

        val client = ClientDAO("miguel", "Miguel Ferreira", "miguel@gmail.com", "pass",
            "924292569", "23124024148", medicalHistoryClient, household)
        clients.save(client)

        medicalHistoryClient.client = client
        medicalHistories.save(medicalHistoryClient)

        household.clients.add(client)
        households.save(household)

        clients.save(client)

        /**
         * Doctor
         */

        val doctor = DoctorDAO("kiko", "Francisco Costa", "kiko@gmail.com", "pass", "921394024")
        doctors.save(doctor)

        /**
         * Appointment
         */
        val appointment = AppointmentDAO(0, LocalDate.now(), AppointmentRegime.IN_PERSON, ScheduleType.APPOINTMENT,
            ScheduleState.SCHEDULED, client, doctor.username, client.medicalHistory)
        appointments.save(appointment)

        medicalHistoryClient.appointments.add(appointment)
        medicalHistories.save(medicalHistoryClient)

        /**
         * Exam
         */
        val exam = ExamDAO(0, LocalDate.now(), "treadmill, more machines", ScheduleType.EXAM, ScheduleState.SCHEDULED,
            client, doctor.username, medicalHistoryClient)
        exams.save(exam)

        medicalHistoryClient.exams.add(exam)
        medicalHistories.save(medicalHistoryClient)




    }


}