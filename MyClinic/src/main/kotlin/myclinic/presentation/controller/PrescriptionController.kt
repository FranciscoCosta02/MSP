package myclinic.presentation.controller

import myclinic.application.PrescriptionApplication
import myclinic.presentation.PrescriptionAPI
import myclinic.presentation.PrescriptionDTO
import myclinic.presentation.prescriptionDAOMapper
import org.springframework.web.bind.annotation.RestController

@RestController
class PrescriptionController(
    private val prescriptions: PrescriptionApplication
): PrescriptionAPI {

    override fun getClientPrescriptions(username: String): List<PrescriptionDTO> =
        prescriptions.getAllClientPrescriptions(username).map { prescriptionDAOMapper(it) }

}