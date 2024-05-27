package myclinic.presentation.controller

import myclinic.application.ClientApplication
import myclinic.presentation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class HouseholdController(
    private val clients: ClientApplication
): HouseholdAPI {

    override fun getClientHousehold(username: String): HouseholdDTO {

        val client = clients.getClient(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) }

        return householdDAOMapper(client.household)
    }

}