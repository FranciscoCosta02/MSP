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

        val client = clientsApp.getClient(appointment.clientUsername).get()
        doctorsApp.getDoctor(appointment.doctorUsername)
            .orElseThrow { NotFoundException(Constants.DOCTOR_NOT_FOUND.message) }

        val appointmentDAO = AppointmentDAO(0, appointment.date, appointment.regime, appointment.scheduleType,
            ScheduleState.SCHEDULED, client, appointment.doctorUsername, medicalHistoryClient, appointment.type)
        medicalHistoryClient.appointments.add(appointmentDAO)
    }

    override fun addExam(username: String, exam: AddExamDTO) {
        val medicalHistoryClient = medicalHistoryApp.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }

        val client = clientsApp.getClient(exam.clientUsername).get()
        doctorsApp.getDoctor(exam.doctorUsername)
            .orElseThrow { NotFoundException(Constants.DOCTOR_NOT_FOUND.message) }

        val examDAO = ExamDAO(0, exam.date, exam.equipment, exam.scheduleType, ScheduleState.SCHEDULED,
            client, exam.doctorUsername, medicalHistoryClient, exam.type)
        medicalHistoryClient.exams.add(examDAO)
    }

    override fun checkIn(username: String, type: String, id: Long) {

        clientsApp.getClient(username).orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }
        if (type == "appointment") {
            appointmentApp.getClientAppointment(username, id)
                .orElseThrow { NotFoundException(Constants.APPOINTMENT_NOT_FOUND.message) }
                .state = ScheduleState.READY

            val appointment = appointmentApp.getClientAppointment(username, id).get()
        }
        else if (type == "exam")
            examApp.getClientExam(username, id)
                .orElseThrow{ NotFoundException(Constants.EXAM_NOT_FOUND.message) }
                .state = ScheduleState.READY
    }
}