package myclinic.application

import myclinic.data.MedicalHistoryDAO
import myclinic.data.MedicalHistoryRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class MedicalHistoryApplication(
    private val medicalHistory: MedicalHistoryRepository
) {

    fun getClientMedicalHistory(username: String): Optional<MedicalHistoryDAO> =
        medicalHistory.findByClientUsername(username)
}