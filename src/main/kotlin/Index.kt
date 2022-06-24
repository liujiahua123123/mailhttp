import kotlinx.coroutines.runBlocking
import net.mamoe.n.HTTPServer
import net.mamoe.n.SMTPServer

class Index {
    companion object{
        @JvmStatic
        fun main(args: Array<String>){
            runBlocking {
                SMTPServer.start()
                HTTPServer.start()
            }
        }
    }
}