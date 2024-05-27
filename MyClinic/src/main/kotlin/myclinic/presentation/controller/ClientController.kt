package myclinic.presentation.controller

import myclinic.application.ClientApplication
import myclinic.application.DoctorApplication
import myclinic.application.MedicalHistoryApplication
import myclinic.data.ClientDAO
import myclinic.data.Speciality
import myclinic.presentation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(
    private val clientApp: ClientApplication,
    private val doctorApp: DoctorApplication,
    private val medicalHistoryApp: MedicalHistoryApplication,
) : ClientAPI {

    override fun addClient(client: ClientDTO) {
        clientApp.addClient(clientDTOMapper(client))
    }

    override fun getClient(username: String): ClientShortDTO {
        val clientDAO = clientApp.getClient(username).orElseThrow{NotFoundException(Constants.CLIENT_NOT_FOUND.message)}
        return clientDAOMapper(clientDAO)
    }

    override fun getRecommendDoctors(username: String): List<DoctorShortDTO> {
        val medicalHistoryClient = medicalHistoryApp.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }

        val map = mutableMapOf<Speciality, Int>()
        var max = 0

        medicalHistoryClient.appointments.forEach {
            val type = it.type
            if (!map.containsKey(type))
                map[type] = 1
            else
                map[type] = map[type]!! + 1;

            if (map[type]!! > max) max = map[type]!!
        }

        medicalHistoryClient.exams.forEach {
            val type = it.type
            if (!map.containsKey(type))
                map[type] = 1
            else
                map[type] = map[type]!! + 1;

            if (map[type]!! > max) max = map[type]!!
        }

        map.forEach {
            println("" + it.key + " " + it.value)
        }

        val maxType = map.keys.filter { map[it] == max }.toMutableList()

        return doctorApp.getDoctors().filter { maxType.contains(it.specialty) }.map { doctorDAOMapper(it) }
    }


}