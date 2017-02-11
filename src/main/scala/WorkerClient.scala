import java.net.Socket
import java.io._
import scala.util.continuations._
import resource._

trait Message

object WorkerClient {
	
	private var writeCallback: String => Unit = s => ()
	private var listener: Message => Unit = m => ()
	
	def connect(host: String, port: Int): Unit = {
		for (connection <- managed(new Socket(host, port));
			outStream <- managed(connection.getOutputStream);
			inStream <- managed(new InputStreamReader(connection.getInputStream))
		) {
			val out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream)))
			val in = new BufferedReader(inStream)
			
			def read(): Unit = in.readLine() match {
				case null => ()
				case line => parse(line); read()
			}
			read()
			
			reset {
				val send = shift((f: String => Unit) => writeCallback = f)
				out.println(send)
			}
		}
	}
	
	private def parse(line: String): Unit = {
		
	}
	
	def write(line: String): Unit = writeCallback(line)
	
	def listen(f: Message => Unit): Unit = listener = f
	
}