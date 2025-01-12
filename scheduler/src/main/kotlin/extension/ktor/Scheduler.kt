package extension.ktor

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

inline fun <T> schedule(
    fixedRate: kotlin.time.Duration? = null,
    fixedDelay: kotlin.time.Duration? = null,
    crossinline block: suspend () -> T,
) {
    if (fixedRate == null && fixedDelay == null) throw IllegalArgumentException("fixedRate or fixedDelay must be provided")
    if (fixedRate != null && fixedDelay != null) throw IllegalArgumentException("fixedRate and fixedDelay cannot be provided at the same time")

    if (fixedRate != null) {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                launch { block.invoke() }
                delay(fixedRate)
            }
        }
    }

    if (fixedDelay != null) {
        CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                block.invoke()
                delay(fixedDelay)
            }
        }
    }
}

inline fun <T> schedule(
    cron: String,
    cronType: CronType = CronType.QUARTZ,
    crossinline block: suspend () -> T,
) =
    CoroutineScope(Dispatchers.Default).launch {
        val cronValue =
            CronParser(
                CronDefinitionBuilder.instanceDefinitionFor(cronType)
            ).parse(cron)

        val executionTime = ExecutionTime.forCron(cronValue)

        while (isActive) {
            val now = ZonedDateTime.now()
            val nextExecution = executionTime.nextExecution(now)

            if (nextExecution.isPresent) {
                val next = nextExecution.get()
                val delayTimeSeconds = ChronoUnit.SECONDS.between(now, next)

                if (delayTimeSeconds > 0) delay(delayTimeSeconds * 1000)
                else block().apply { delay(1000) }
            }
        }
    }