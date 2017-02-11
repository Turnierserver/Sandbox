import java.io.File

import compilers._
import net.lingala.zip4j.core.ZipFile
import util._

import scala.collection.mutable

package object sandbox {
	
	case class Run(lang: Language, zip: ZipFile)
	
	def run(r: Run): Option[String] = {
		val ai = extract(r.zip)
		val properties = readProperties(ai)
		val command = createCommand(r.lang, properties)
		
		if(!Isolate.init(1))
			Some("Initialization of Isolate failed!")
		else if(!Isolate.run(1, ai, command))
			Some("Walltime or memory-limit exceeded!")
		else if(!Isolate.cleanup(1))
			Some("Cleanup of Isolate failed!")
		else
			None
	}
	
	private def createCommand(lang: Language, properties: mutable.Map[String, String]): String = lang match {
		case Java() =>
			s"java ${properties("main-class")}"
		case Scala() =>
			s"scala ${properties("main-class")}"
		case Cpp() =>
			"./ai"
		case Python() =>
			s"python${properties("version")} ${properties("main-file")}"
		case Rust() =>
			s"./ai"
		case Go() =>
		case Haskell() =>
	}
	
}
