package net.mamoe.n

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.html.currentTimeMillis
import kotlinx.serialization.Serializable
import java.io.File
import java.util.*
import kotlin.concurrent.thread


@Serializable
data class StorableEmail(
    val id: String = createUuid4(),
    val title: String,
    val from: String,
    val to: String,
    val content: String,
    val remoteAddress: String,
)

object EmailStorageService {
    private val emailsFolder = System.getProperty("user.dir") + "/data/"

    init {
        File(emailsFolder).mkdirs()
    }

    fun get(address: String): StorableEmail? {
        val file = getFileFor(address);
        if (file.exists()) {
            return file.deserialize()
        }
        return null
    }

    private fun getFileFor(address: String): File {
        return File("$emailsFolder${address.lowercase(Locale.getDefault())}.json")
    }

    fun save(email: StorableEmail) {
        getFileFor(email.to).apply {
            if (!this.exists()) {
                this.createNewFile()
                this.deleteOnExit()
            }
        }.writeData(email)

        EmailDeleteService.push(EmailDeleteTask(
            email.to,
            currentTimeMillis() + 1000 * 60 * 10,
            email.id
        ))
    }

    data class EmailDeleteTask(
        val address: String,
        val deleteAt: Long,
        val emailUUID: String
    )

    object EmailDeleteService {
        private val tasks: Channel<EmailDeleteTask> = Channel(Channel.UNLIMITED)

        init {
            CoroutineScope(Job()).launch {
                while (isActive) {
                    val task = tasks.receive()
                    val diff = task.deleteAt - currentTimeMillis()
                    delay(diff)
                    handle(task)
                }
            }
        }

        private fun handle(task: EmailDeleteTask, force: Boolean = false) {
            val emailFile = getFileFor(task.address)
            if (!emailFile.exists()) {
                return
            }
            if (force) {
                emailFile.delete()
                return
            }
            val email: StorableEmail? = emailFile.deserialize()
            if (email == null || email.id == task.emailUUID) {
                emailFile.delete()
            }
        }

        fun push(task: EmailDeleteTask) = runBlocking { tasks.send(task) }
    }
}









