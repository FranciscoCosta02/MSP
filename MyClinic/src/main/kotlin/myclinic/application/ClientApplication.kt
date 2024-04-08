package myclinic.application

import myclinic.data.AppointmentDAO
import myclinic.data.ClientDAO
import myclinic.data.ClientRepository
import myclinic.presentation.AddClientDTO
import myclinic.presentation.ClientShortDTO
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ClientApplication(
    val clients: ClientRepository
) {

    fun addClient(client: ClientDAO) {
        clients.save(client)
    }

    fun getClient(username: String): Optional<ClientDAO> =
        clients.findByUsername(username)

}