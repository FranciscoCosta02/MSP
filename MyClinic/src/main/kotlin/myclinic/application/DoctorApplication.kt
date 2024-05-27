package myclinic.application

import myclinic.data.DoctorDAO
import myclinic.data.DoctorRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class DoctorApplication(
    private val doctors: DoctorRepository
) {

    fun addDoctor(doctor: DoctorDAO) {
        doctors.save(doctor)
    }

    fun getDoctor(username: String): Optional<DoctorDAO> =
        doctors.findByUsername(username)

    fun getDoctors(): Iterable<DoctorDAO> =
        doctors.findAll()

}