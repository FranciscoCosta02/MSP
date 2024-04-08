package myclinic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyClinicApplication

fun main(args: Array<String>) {
    runApplication<MyClinicApplication>(*args)
}
