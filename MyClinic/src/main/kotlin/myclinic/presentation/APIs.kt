package myclinic.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@Tag(name = "Doctors", description = "Doctors API")
interface DoctorsAPI {

    @PostMapping("doctors")
    fun addDoctor(@RequestBody doctor: DoctorDTO)

}

@RequestMapping("/api")
@Tag(name = "Clients", description = "Clients API")
interface ClientAPI {

    @PostMapping("clients")
    fun addClient(@RequestBody client: ClientDTO)



    @GetMapping("clients/{username}")
    fun getClient(@PathVariable username: String): ClientShortDTO

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