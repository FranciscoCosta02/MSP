package myclinic.presentation.controller

import myclinic.application.DoctorApplication
import myclinic.presentation.DoctorDTO
import myclinic.presentation.DoctorShortDTO
import myclinic.presentation.DoctorsAPI
import myclinic.presentation.doctorDAOMapper
import org.springframework.web.bind.annotation.RestController

@RestController
class DoctorController(
    private val doctors: DoctorApplication
): DoctorsAPI {

    override fun addDoctor(doctor: DoctorDTO) {
        TODO("Not yet implemented")
    }

    override fun getDoctors(): List<DoctorShortDTO> =
        doctors.getDoctors().map { doctorDAOMapper(it) }
}