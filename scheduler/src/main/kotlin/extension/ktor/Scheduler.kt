package extension.ktor

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import kotlinx.coroutines.*
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun <T> schedule(
    cron: String,
    cronType: CronType = CronType.QUARTZ,
    block: suspend () -> T,
) =
    CoroutineScope(Dispatchers.Default).launch {
        val cronValue = cron.toCronValue(cronType)
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

private fun String.toCronValue(
    cronType: CronType,
) =
    CronParser(
        CronDefinitionBuilder.instanceDefinitionFor(cronType)
    ).parse(this)