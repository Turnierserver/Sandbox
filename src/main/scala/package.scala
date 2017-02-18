import java.io.File
import java.nio.file.Files

import net.lingala.zip4j.core._
import util._

import scala.collection._
import scala.sys.process._
import scala.util._
import scalaz.Scalaz._


package object compilers {
	
	sealed trait Language
	
	case class Compile (lang: Language, zip: File)
	
	case class Library (lang: Language, name: String, version: String)
	
	case object Java extends Language
	
	case object Scala extends Language
	
	case object Cpp extends Language
	
	case object Python extends Language
	
	case object Rust extends Language
	
	case object Go extends Language
	
	case object Haskell extends Language
	
	def compile (comp: Compile, fetchLibrary: Library => File, logger: String => Unit): Option[ZipFile] = Try {
		val folder = extract (new ZipFile (comp.zip.getAbsolutePath))
		val propertiesFile = new File (folder, "properties.prop")
		val librariesFile = new File (folder, "libraries.txt")
		
		if (!propertiesFile.exists) {
			logger ("properties.prop existiert nicht!")
			none
		}
		else if (!librariesFile.exists) {
			logger ("libraries.txt existiert nicht!")
			none
		}
		else {
			val properties = readProperties (propertiesFile)
			val libraries = readLibraries (librariesFile, comp.lang) ∘ fetchLibrary
			val target = Files createTempDirectory "turnierserver-compiler-target" toFile
			val command = createCommand (comp.lang, properties, libraries, folder, target)
			logger ("Compiling with command " + command.toString.replace(",", ""))
			if (command ! ProcessLogger (logger) == 0) {
				Files copy(propertiesFile toPath, new File (target, "properties.prop") toPath)
				Files copy(propertiesFile toPath, new File (target, "libraries.txt") toPath)
				zip ("turnierserver-compiled", target).some
			}
			else none
		}
	} recover {
		case e =>
			logger ("Failed with exception:\n" + getStackTrace (e))
			none
	} getOrElse none
	
	private def createCommand (lang: Language, properties: mutable.Map[String, String], libraries: List[File], source: File, target: File): ProcessBuilder = lang match {
		case Java =>
			val classpath = join ((libraries >>= { recChildren (_, "jar") }) ∘ { f => wrap (f.getAbsolutePath) }, ":")
			val cp = if(classpath.isEmpty) "" else "-cp " + classpath
			val files = join (recChildren (source, "java") ∘ { f => wrap (f.getAbsolutePath) }, " ")
			osPrefix + s"javac $cp -d ${wrap (target.toString) } $files"
		case Scala =>
			val classpath = join ((libraries >>= { recChildren (_, "jar") }) ∘ { f => wrap (f.getAbsolutePath) }, ":")
			val cp = if(classpath.isEmpty) "" else "-cp " + classpath
			val files = join (recChildren (source, "scala") ∘ { f => wrap (f.getAbsolutePath) }, " ")
			osPrefix + s"scalac $cp -d ${wrap (target.toString) } $files"
		case Cpp =>
			"echo 'aaay cpp'"
		case Python =>
			"echo 'aaay python'"
		case Rust =>
			"echo 'aaay rust'"
		case Go =>
			"echo 'aaay go'"
		case Haskell =>
			"echo 'aaay haskell'"
	}
	
	private def osPrefix =
		sys props ("os.name") toLowerCase match {
			case x if x contains "windows" => "cmd /C "
			case _ => ""
		}
	
}

