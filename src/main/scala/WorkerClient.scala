import java.net.Socket
import java.io._
import resource._
import scalaz._, Scalaz._

trait Message

object WorkerClient {
	
	var out: Option[PrintWriter] = none
	var in: Option[BufferedReader] = none
	private var listener: Option[Message => Unit] = None
	
	def connect(host: String, port: Int): Unit = {
		for (connection <- managed(new Socket(host, port));
			outStream <- managed(connection.getOutputStream);
			inStream <- managed(new InputStreamReader(connection.getInputStream))
		) {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream))).some
			in = new BufferedReader(inStream).some
			
			def read(): Unit = (in ∘ {_.readLine}) | null match {
				case null => ()
				case line => parse(line); read()
			}
			read()
		}
	}
	
	private def parse(line: String): Unit = {
		
	}
	
	def write(line: String): Unit = out foreach {_ println line}
	
	def listen(f: Message => Unit): Unit = listener = f.some
	
}