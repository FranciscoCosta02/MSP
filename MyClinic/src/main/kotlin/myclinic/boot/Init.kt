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
            val prescriptions: PrescriptionRepository): CommandLineRunner
{

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
        val doctor1 = DoctorDAO("kiko", "Francisco", "kiko@gmail.com", "pass", "921394024", Speciality.CARDIO)
        val doctor2 = DoctorDAO("rita", "Rita", "rita@gmail.com", "pass", "921394024", Speciality.ENT)
        val doctor3 = DoctorDAO("fred", "Frederico", "frederico@gmail.com", "pass", "921394024", Speciality.CARDIO)
        val doctor4 = DoctorDAO("pedro", "Pedro", "pedro@gmail.com", "pass", "921394024", Speciality.ORTHO)
        val doctor5 = DoctorDAO("joao", "João", "pedro@gmail.com", "pass", "921394024", Speciality.ORTHO)
        doctors.saveAll(listOf(doctor1, doctor2, doctor3, doctor4, doctor5))

        /**
         * Appointment
         */
        val appointment1 = AppointmentDAO(0, LocalDate.now(), AppointmentRegime.IN_PERSON, ScheduleType.APPOINTMENT,
            ScheduleState.SCHEDULED, client, doctor1.username, client.medicalHistory, doctor1.specialty)
        val appointment2 = AppointmentDAO(0, LocalDate.now(), AppointmentRegime.ONLINE, ScheduleType.EXAM,
            ScheduleState.SCHEDULED, client, doctor2.username, client.medicalHistory, doctor2.specialty)
        val appointment3 = AppointmentDAO(0, LocalDate.now(), AppointmentRegime.IN_PERSON, ScheduleType.APPOINTMENT,
            ScheduleState.SCHEDULED, client, doctor3.username, client.medicalHistory, doctor3.specialty)

        val appointmentList = listOf(appointment1, appointment2, appointment3)
        appointments.saveAll(appointmentList)
        medicalHistoryClient.appointments.addAll(appointmentList)
        medicalHistories.save(medicalHistoryClient)

        /**
         * Exam
         */
        val exam1 = ExamDAO(0, LocalDate.now(), "treadmill, more machines", ScheduleType.EXAM, ScheduleState.SCHEDULED,
            client, doctor4.username, medicalHistoryClient, doctor4.specialty)
        val exam2 = ExamDAO(0, LocalDate.now(), "a couple machines", ScheduleType.EXAM, ScheduleState.SCHEDULED,
            client, doctor5.username, medicalHistoryClient, doctor5.specialty)

        val examList = listOf(exam1, exam2)
        exams.saveAll(examList)
        medicalHistoryClient.exams.addAll(examList)
        medicalHistories.save(medicalHistoryClient)

        /**
         * Prescriptions
         */
        val prescription1 = PrescriptionDAO(0, "some medication info", client, doctor1.username,
            medicalHistoryClient)
        val prescription2 = PrescriptionDAO(0, "some more info", client, doctor5.username,
            medicalHistoryClient)

        val prescriptionList = listOf(prescription1, prescription2)
        prescriptions.saveAll(prescriptionList)
        medicalHistoryClient.prescriptions.addAll(prescriptionList)
        medicalHistories.save(medicalHistoryClient)

    }


}