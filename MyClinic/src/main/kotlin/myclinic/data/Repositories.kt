package myclinic.data

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ClientRepository: CrudRepository<ClientDAO, Long> {

    fun findByUsername(@Param("username") username: String): Optional<ClientDAO>

}

interface DoctorRepository: CrudRepository<DoctorDAO, Long> {

    fun findByUsername(@Param("username") username: String): Optional<DoctorDAO>

}

interface MedicalHistoryRepository: CrudRepository<MedicalHistoryDAO, Long> {

    fun findByClientUsername(@Param("username") username: String): Optional<MedicalHistoryDAO>

}

interface HouseholdRepository: CrudRepository<HouseholdDAO, Long> {

}

interface AppointmentRepository: CrudRepository<AppointmentDAO, Long> {

    fun findAllByClientUsername(@Param("username") username: String): Iterable<AppointmentDAO>

    fun findByClientUsernameAndId(@Param("username") username: String, @Param("id") id: Long): Optional<AppointmentDAO>

 }

interface ExamRepository: CrudRepository<ExamDAO, Long> {

    fun findAllByClientUsername(@Param("username") username: String): Iterable<ExamDAO>

    fun findByClientUsernameAndId(@Param("username") username: String, @Param("id") id: Long): Optional<ExamDAO>
}

interface PrescriptionRepository: CrudRepository<PrescriptionDAO, Long> {

    fun findAllByClientUsername(@Param("username") username: String): Iterable<PrescriptionDAO>

}

