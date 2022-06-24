package net.mamoe.n

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.slf4j.LoggerFactory

object HTTPServer {
    @Serializable
    data class GetEmailResponse(
        val success: Boolean,
        val email: StorableEmail?
    )

    private val server: NettyApplicationEngine = embeddedServer(Netty, environment = applicationEngineEnvironment {
        // this.parentCoroutineContext
        this.connector {
            host = "0.0.0.0"
            port = 9661
            println("[HTTP] HTTPAPI Service running on http://0.0.0.0:9661")
        }
        this.log = LoggerFactory.getLogger("Server")
        module {
            routing {
                get("/") {
                    call.respondOutputStream(contentType = ContentType.Text.Html) {
                        HTTPServer.javaClass.classLoader.getResourceAsStream("main.html")
                            ?.use { input -> input.copyTo(this) }
                    }
                }

                get("/receive"){
                    try {
                        val address = call.parameters["address"]
                        if (address == null){
                            call.respond(HttpStatusCode.BadRequest, "Missing address parameter")
                            return@get
                        }
                        val email = EmailStorageService.get(address)
                        call.respond(
                            HttpStatusCode.OK, Json.encodeToString(
                                GetEmailResponse(email != null, email)
                            )
                        )
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    })


    fun start() {
        server.start(true)
    }
}

