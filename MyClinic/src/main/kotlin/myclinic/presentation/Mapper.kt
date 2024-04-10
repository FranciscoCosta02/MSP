package myclinic.presentation

import myclinic.data.*

fun clientDTOMapper(client: ClientDTO): ClientDAO {

    val mhDTO = client.medicalHistoryDTO
    val hh = client.householdDTO

    val mhDAO = MedicalHistoryDAO(mhDTO.id, null, mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    val hhDAO = HouseholdDAO(hh.id, mutableListOf())

    val clientDAO = ClientDAO(client.username, client.name, client.email, client.password, client.phone, client.nif, mhDAO, hhDAO)

    clientDAO.medicalHistory.client = clientDAO
    clientDAO.household.clients.add(clientDAO)

    return clientDAO
}

fun clientDAOMapper(client: ClientDAO): ClientShortDTO =
    ClientShortDTO(client.username, client.email, client.phone)

fun appointmentDAOMapperToSchedule(appointment: AppointmentDAO): ScheduleDTO =
    ScheduleDTO(appointment.id,
        ClientShortDTO(appointment.client.username, appointment.client.email, appointment.client.phone),
        DoctorShortDTO(appointment.doctor.username, appointment.doctor.email, appointment.doctor.phone),
        appointment.state, appointment.date, appointment.type)

fun examDAOMapperToSchedule(exam: ExamDAO): ScheduleDTO =
    ScheduleDTO(exam.id,
        ClientShortDTO(exam.client.username, exam.client.email, exam.client.phone),
        DoctorShortDTO(exam.doctor.username, exam.doctor.email, exam.doctor.phone),
        exam.state, exam.date, exam.type)