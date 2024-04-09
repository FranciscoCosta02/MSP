package myclinic.presentation.controller

import myclinic.application.ClientApplication
import myclinic.data.ClientDAO
import myclinic.presentation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(
    private val clientApp: ClientApplication
) : ClientAPI {

    override fun addClient(client: ClientDTO) {
        clientApp.addClient(clientDTOMapper(client))
    }

    override fun getClient(username: String): ClientShortDTO {
        val clientDAO = clientApp.getClient(username).orElseThrow{NotFoundException(Constants.CLIENT_NOT_FOUND.message)}
        return clientDAOMapper(clientDAO)
    }


}