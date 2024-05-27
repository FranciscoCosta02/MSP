package myclinic.application

import myclinic.data.PrescriptionDAO
import myclinic.data.PrescriptionRepository
import org.springframework.stereotype.Service

@Service
class PrescriptionApplication(
    private val prescriptions: PrescriptionRepository
) {

    fun addPrescription(prescription: PrescriptionDAO) {
        prescriptions.save(prescription)
    }

    fun getAllClientPrescriptions(username: String): Iterable<PrescriptionDAO> =
        prescriptions.findAllByClientUsername(username)

}