package extension.ktor

import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec

class SchedulerTest: StringSpec({
    "스케줄러가 정해진 시간에 실행되는지 확인한다." {
        runBlocking {
//            schedule("0 27 16 * * ?") {
//                println("스케줄러가 정해진 시간에 실행되었습니다.")
//            }.join()
        }
    }
})