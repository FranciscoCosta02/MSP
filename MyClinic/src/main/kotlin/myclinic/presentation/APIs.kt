package myclinic.presentation

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@Tag(name = "Doctors", description = "Doctors API")
interface DoctorsAPI {

    @PostMapping("doctors")
    fun addDoctor(@RequestBody doctor: DoctorDTO)

    @GetMapping("doctors")
    fun getDoctors(): List<DoctorShortDTO>

}

@RequestMapping("/api")
@Tag(name = "Clients", description = "Clients API")
interface ClientAPI {

    @PostMapping("clients")
    fun addClient(@RequestBody client: ClientDTO)

    @GetMapping("clients/{username}")
    fun getClient(@PathVariable username: String): ClientShortDTO

    @GetMapping("clients/{username}/doctors/recommended")
    fun getRecommendDoctors(@PathVariable username: String): List<DoctorShortDTO>

}

@RequestMapping("/api")
@Tag(name = "Schedule", description = "Schedule API")
interface ScheduleAPI {

    @GetMapping("clients/{username}/schedule")
    fun getClientSchedule(@PathVariable username: String): List<ScheduleDTO>

    @PostMapping("clients/{username}/appointments")
    fun addAppointment(@PathVariable username: String, @RequestBody appointment: AddAppointmentDTO)

    @PostMapping("clients/{username}/exams")
    fun addExam(@PathVariable username: String, @RequestBody exam: AddExamDTO)

    @PutMapping("clients/{username}/schedule/{type}/{id}")
    fun checkIn(@PathVariable username: String, @PathVariable type: String, @PathVariable id: Long)

}

@RequestMapping("/api")
@Tag(name = "Prescription", description = "Prescription API")
interface PrescriptionAPI {

    @GetMapping("clients/{username}/prescriptions")
    fun getClientPrescriptions(@PathVariable username: String): List<PrescriptionDTO>

}

@RequestMapping("/api")
@Tag(name = "Household", description = "Household API")
interface HouseholdAPI {

    @GetMapping("clients/{username}/household")
    fun getClientHousehold(@PathVariable username: String): HouseholdDTO

}

@RequestMapping("/api")
@Tag(name = "Medical History", description = "Medical History")
interface MedicalHistoryAPI {

    @GetMapping("clients/{username}/medical_history")
    fun getClientMedicalHistory(@PathVariable username: String): MedicalHistoryDTO
    
}