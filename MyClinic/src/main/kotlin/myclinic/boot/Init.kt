package myclinic.boot

import myclinic.data.*
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

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
        val medicalHistoryClient = MedicalHistoryDAO(0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf())
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
        val medicalHistoryDoctor = MedicalHistoryDAO(0, null, null,
            mutableListOf(), mutableListOf(), mutableListOf())
        medicalHistories.save(medicalHistoryDoctor)

        val doctor = DoctorDAO("kiko", "Francisco Costa", "kiko@gmail.com", "pass",
            "921394024", medicalHistoryDoctor)
        doctors.save(doctor)

        medicalHistoryDoctor.doctor = doctor
        medicalHistories.save(medicalHistoryDoctor)

        doctors.save(doctor)





    }


}