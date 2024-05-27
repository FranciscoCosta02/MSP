package myclinic.presentation.controller

import myclinic.application.MedicalHistoryApplication
import myclinic.presentation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class MedicalHistoryController(
    private val medicalHistories: MedicalHistoryApplication
): MedicalHistoryAPI {

    override fun getClientMedicalHistory(username: String): MedicalHistoryDTO =
        medicalHistoryDAOMapper(medicalHistories.getClientMedicalHistory(username)
            .orElseThrow { NotFoundException(Constants.CLIENT_NOT_FOUND.message) })

}