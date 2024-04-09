package myclinic.application

import myclinic.data.ExamDAO
import myclinic.data.ExamRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ExamApplication(
    private val exams: ExamRepository
) {

    fun addExam(exam: ExamDAO) {
        exams.save(exam)
    }

    fun getAllClientExams(username: String): Iterable<ExamDAO> =
        exams.findAllByClientUsername(username)

    fun getClientExam(username: String, id: Long): Optional<ExamDAO> =
        exams.findByClientUsernameAndId(username, id)

}