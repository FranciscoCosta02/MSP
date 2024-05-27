package myclinic.presentation

import myclinic.data.*

fun clientDTOMapper(client: ClientDTO): ClientDAO {

    val mhDTO = client.medicalHistoryDTO
    val hh = client.householdDTO

    val mhDAO = MedicalHistoryDAO(mhDTO.id, null, mutableListOf(), mutableListOf(), mutableListOf())
    val hhDAO = HouseholdDAO(hh.id, mutableListOf())

    val clientDAO = ClientDAO(client.username, client.name, client.email, client.password, client.phone, client.nif, mhDAO, hhDAO)

    clientDAO.medicalHistory.client = clientDAO
    clientDAO.household.clients.add(clientDAO)

    return clientDAO
}

fun clientDAOMapper(client: ClientDAO): ClientShortDTO =
    ClientShortDTO(client.username, client.email, client.phone)

fun doctorDAOMapper(doctor: DoctorDAO): DoctorShortDTO =
    DoctorShortDTO(doctor.username, doctor.email, doctor.phone, doctor.specialty)

fun appointmentDAOMapperToSchedule(appointment: AppointmentDAO): ScheduleDTO =
    ScheduleDTO(appointment.id,
        ClientShortDTO(appointment.client.username, appointment.client.email, appointment.client.phone),
        appointment.doctor, appointment.state, appointment.date, appointment.scheduleType, appointment.type)

fun examDAOMapperToSchedule(exam: ExamDAO): ScheduleDTO =
    ScheduleDTO(exam.id,
        ClientShortDTO(exam.client.username, exam.client.email, exam.client.phone),
        exam.doctor, exam.state, exam.date, exam.scheduleType, exam.type)

fun prescriptionDAOMapper(prescription: PrescriptionDAO): PrescriptionDTO =
    PrescriptionDTO(prescription.id, prescription.content, prescription.client.username, prescription.doctor)

fun medicalHistoryDAOMapper(medicalHistory: MedicalHistoryDAO): MedicalHistoryDTO {

    val appointments = medicalHistory.appointments.map { appointmentDAOMapperToSchedule(it) }
    val exams = medicalHistory.exams.map { examDAOMapperToSchedule(it) }
    val prescriptions = medicalHistory.prescriptions.map { prescriptionDAOMapper(it) }

    return MedicalHistoryDTO(medicalHistory.id, medicalHistory.client!!.username, appointments, exams, prescriptions)
}

fun householdDAOMapper(household: HouseholdDAO): HouseholdDTO {

    val clients = household.clients.map { clientDAOMapper(it) }

    return HouseholdDTO(household.id, clients)
}
