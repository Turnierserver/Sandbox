
import compilers._
import net.lingala.zip4j.core.ZipFile
import util._

import scala.collection.mutable
import scala.util.Try
import scalaz.Scalaz._

package object sandbox {
	
	case class Run (lang: Language, zip: ZipFile)
	
	def run (r: Run): Option[String] = Try {
		val ai = extract (r.zip)
		val properties = readProperties (ai)
		val command = createCommand (r.lang, properties)
		
		if (!(Isolate init 1))
			"Initialization of Isolate failed!".some
		else if (!(Isolate run(1, ai, command)))
			"Walltime or memory-limit exceeded!".some
		else if (!(Isolate cleanup 1))
			"Cleanup of Isolate failed!".some
		else
			none
	} recover {
		case e => ("Failed with exception:\n " + getStackTrace (e)).some
		
	} getOrElse "Unknown Error!".some
	
	
	private def createCommand (lang: Language, properties: mutable.Map[String, String]): String = lang match {
		case Java =>
			s"java ${properties ("main-class") }"
		case Scala =>
			s"scala ${properties ("main-class") }"
		case Cpp =>
			"./ai"
		case Python =>
			s"python${properties ("version") } ${properties ("main-file") }"
		case Rust =>
			s"./ai"
		case Go =>
			""
		case Haskell =>
			""
	}
}
