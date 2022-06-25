package net.mamoe.n

import kotlinx.html.currentTimeMillis
import org.apache.james.mime4j.codec.DecodeMonitor
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder
import org.apache.james.mime4j.parser.ContentHandler
import org.apache.james.mime4j.parser.MimeStreamParser
import org.apache.james.mime4j.stream.BodyDescriptorBuilder
import org.apache.james.mime4j.stream.MimeConfig
import org.subethamail.smtp.server.SMTPServer
import tech.blueglacier.parser.CustomContentHandler
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

object SMTPServer {
    private val server = SMTPServer
        .port(25)
        .messageHandler { context, from, to, data ->
            try {
                val receiver = to.lowercase(Locale.getDefault())

                val contentHandler: ContentHandler = CustomContentHandler()

                val mime4jParserConfig = MimeConfig.DEFAULT
                val bodyDescriptorBuilder: BodyDescriptorBuilder = DefaultBodyDescriptorBuilder()
                val mime4jParser = MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder)
                mime4jParser.isContentDecoding = true
                mime4jParser.setContentHandler(contentHandler)

                val mailIn: InputStream = data.inputStream()
                mime4jParser.parse(mailIn)

                val parsedMail = (contentHandler as CustomContentHandler).email

                var body = "empty body"

                val primaryHtmlBodyBytes = parsedMail.htmlEmailBody?.`is`?.readBytes()
                if (primaryHtmlBodyBytes != null) {
                    body = String(primaryHtmlBodyBytes, Charset.forName("UTF-8"))
                }
                if (body.isBlank()) {
                    val secondaryPlainTextBody = parsedMail.plainTextEmailBody?.`is`?.readBytes()
                    if (secondaryPlainTextBody != null) {
                        body = String(secondaryPlainTextBody, Charset.forName("UTF-8"))
                    }
                }
                val mail = StorableEmail(
                    from = from,
                    title = parsedMail.emailSubject ?: "untitled",
                    to = receiver,
                    content = body,
                    remoteAddress = context.remoteAddress.toString()
                )

                EmailStorageService.save(mail)

                println("[SMTP] One Email received for $receiver")
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
        .build()

    fun start() {
        println("[SMTP] MailReceiver running on http://0.0.0.0:25")
        server.start()
    }
}