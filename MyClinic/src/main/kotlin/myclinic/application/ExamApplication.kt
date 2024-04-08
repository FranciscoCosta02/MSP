package myclinic.application

import myclinic.data.ExamDAO
import myclinic.data.ExamRepository

class ExamApplication(
    val exams: ExamRepository
) {

    fun addExam(exam: ExamDAO) {
        exams.save(exam)
    }

    fun getAllClientExams(username: String): Iterable<ExamDAO> =
        exams.findAllByClientUsername(username)

}