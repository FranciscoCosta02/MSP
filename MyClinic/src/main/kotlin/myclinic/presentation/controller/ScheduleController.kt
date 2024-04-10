package myclinic.presentation.controller

import myclinic.application.*
import myclinic.data.AppointmentDAO
import myclinic.data.ExamDAO
import myclinic.data.ScheduleState
import myclinic.presentation.*
import org.aspectj.weaver.ast.Not
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.optionals.getOrElse

@RestController
class ScheduleController(
    private val clientsApp: ClientApplication,
    private val doctorsApp: DoctorApplication,
    private val medicalHistoryApp: MedicalHistoryApplication,
    private val appointmentApp: AppointmentApplication,
    private val examApp: ExamApplication
) : ScheduleAPI {

    override fun getClientSchedule(username: String): List<ScheduleDTO> {
        val medicalHistory = medicalHistoryApp.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }

        val appointments = medicalHistory.appointments.map { appointmentDAOMapperToSchedule(it) }
        val exams = medicalHistory.exams.map { examDAOMapperToSchedule(it) }

        val schedule = mutableListOf<ScheduleDTO>()
        schedule.addAll(appointments)
        schedule.addAll(exams)

        return schedule
    }

    override fun addAppointment(username: String, appointment: AddAppointmentDTO) {
        val medicalHistoryClient = medicalHistoryApp.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }
        val medicalHistoryDoctor = medicalHistoryApp.getClientMedicalHistory(appointment.doctorUsername)
            .orElseThrow{ NotFoundException(Constants.DOCTOR_NOT_FOUND.message) }

        val client = clientsApp.getClient(appointment.clientUsername).get()
        val doctor = doctorsApp.getDoctor(appointment.doctorUsername).get()

        val appointmentDAO = AppointmentDAO(0, appointment.date, appointment.regime, appointment.type,
            ScheduleState.SCHEDULED, client, doctor, medicalHistoryClient, medicalHistoryDoctor)
        medicalHistoryClient.appointments.add(appointmentDAO)
        medicalHistoryDoctor.appointments.add(appointmentDAO)
    }

    override fun addExam(username: String, exam: AddExamDTO) {
        val medicalHistoryClient = medicalHistoryApp.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }
        val medicalHistoryDoctor = medicalHistoryApp.getClientMedicalHistory(exam.doctorUsername)
            .orElseThrow{ NotFoundException(Constants.DOCTOR_NOT_FOUND.message) }

        val client = clientsApp.getClient(exam.clientUsername).get()
        val doctor = doctorsApp.getDoctor(exam.doctorUsername).get()

        val examDAO = ExamDAO(0, exam.date, exam.equipment, exam.type, ScheduleState.SCHEDULED,
            client, doctor, medicalHistoryClient, medicalHistoryDoctor)
        medicalHistoryClient.exams.add(examDAO)
        medicalHistoryDoctor.exams.add(examDAO)
    }

    override fun checkIn(username: String, type: String, id: Long) {

        clientsApp.getClient(username).orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }
        if (type == "appointment")
            appointmentApp.getClientAppointment(username, id)
                .orElseThrow { NotFoundException(Constants.APPOINTMENT_NOT_FOUND.message) }
                .state = ScheduleState.READY
        else if (type == "exam")
            examApp.getClientExam(username, id)
                .orElseThrow{ NotFoundException(Constants.EXAM_NOT_FOUND.message) }
                .state = ScheduleState.READY
    }
}