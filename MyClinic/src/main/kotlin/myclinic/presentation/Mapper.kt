package myclinic.presentation

import myclinic.data.ClientDAO
import myclinic.data.MedicalHistoryDAO

fun clientDAOMapper(client: AddClientDTO): ClientDAO {

    val mh = client.medicalHistoryDTO
    val hh = client.householdDTO

    return ClientDAO(client.username, client.name, client.email, client.password, client.phone, client.nif,)
}