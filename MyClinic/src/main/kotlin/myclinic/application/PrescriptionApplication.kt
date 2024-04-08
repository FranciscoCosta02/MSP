package myclinic.application

import myclinic.data.PrescriptionDAO
import myclinic.data.PrescriptionRepository

class PrescriptionApplication(
    val prescriptions: PrescriptionRepository
) {

    fun addPrescription(prescription: PrescriptionDAO) {
        prescriptions.save(prescription)
    }


}