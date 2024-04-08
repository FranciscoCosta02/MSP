package myclinic.presentation.controller

import myclinic.application.ClientApplication
import myclinic.data.ClientDAO
import myclinic.presentation.AddClientDTO
import myclinic.presentation.ClientAPI
import myclinic.presentation.ClientShortDTO
import org.springframework.web.bind.annotation.RestController

@RestController
class ClientController(
    val clientApp: ClientApplication
) : ClientAPI {

    override fun addClient(client: AddClientDTO) {
        TODO("Not yet implemented")
    }

    override fun getClient(username: String): ClientShortDTO {
        TODO("Not yet implemented")
    }


}