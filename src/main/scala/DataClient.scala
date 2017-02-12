import java.io._
import java.net.Socket

import resource.managed

import scalaz._, Scalaz._

object DataClient {
	
	var out: Option[PrintWriter] = none
	var in: Option[BufferedReader] = none
	
	def connect(host: String, port: Int): Unit = {
		for (connection <- managed(new Socket(host, port));
		     outStream <- managed(connection.getOutputStream);
		     inStream <- managed(new InputStreamReader(connection.getInputStream))
		) {
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outStream))).some
			in = new BufferedReader(inStream).some
			while(true) ()
		}
	}
	
	def send(s: String): Array[Byte] = {
		out foreach {_ println s}
		(in ∘ {_ readLine() getBytes}) | Array[Byte]()
	}
	
}
