package extension.ktor.exposed

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> reactiveTransaction(
    block: suspend () -> T,
) = newSuspendedTransaction(Dispatchers.IO) { block() }