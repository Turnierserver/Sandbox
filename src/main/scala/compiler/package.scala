import java.io.File
import java.nio.file.Files
import net.lingala.zip4j.core._
import scala.sys.process._
import scala.collection._
import util._
import scalaz._, Scalaz._

package object compilers {
	
    case class Compile(lang: Language, zip: File)
    
    sealed trait Language
    case object Java extends Language
    case object Scala extends Language
    case object Cpp extends Language
    case object Python extends Language
    case object Rust extends Language
    case object Go extends Language
    case object Haskell extends Language
    
    case class Library(lang: Language, name: String, version: String)
    
    
    def compile(comp: Compile, fetchLibrary: Library => File, logger: String => Unit): Option[ZipFile] = {
        val folder = extract(new ZipFile(comp.zip.getAbsolutePath))
        val propertiesFile = new File(folder, "properties.prop")
        val librariesFile = new File(folder, "properties.txt")
	    
        if(!propertiesFile.exists) {
            logger("properties.prop existiert nicht!")
            none
        }
        else if(!librariesFile.exists) {
            logger("libraries.txt existiert nicht!")
            none
        }
        else {
	        val properties = readProperties(propertiesFile)
	        val libraries = readLibraries(librariesFile, comp.lang) ∘ fetchLibrary
	        val target = Files createTempDirectory "turnierserver-compiler-target" toFile
	        val command = createCommand(comp.lang, properties, libraries, folder, target)
	        logger("Compiling with command " + command.toString)
	        if (command ! ProcessLogger(logger) == 0) {
		        Files copy (propertiesFile toPath, new File(target, "properties.prop") toPath)
		        zip(target).some
	        }
	        else none
        }
    }
    
    private def readLibraries(file: File, lang: Language): List[Library] = streamToList(Files lines file.toPath) ∘ {_.split("/")} >>= { a =>
        if(a.length != 2) Nil
        else List(Library(lang, a(0), a(1)))
    }

    private def createCommand(lang: Language, properties: mutable.Map[String, String], libraries: List[File], source: File, target: File): ProcessBuilder = lang match {
        case Java =>
            val classpath = join("." :: ((libraries >>= {recChildren(_, "jar")}) ∘ {_.getAbsolutePath}), ":")
            val files = join(recChildren(source, "java") ∘ {_.getAbsolutePath}, " ")
            s"javac -cp $classpath -d ${target.toString} $files"
        case Scala =>
            val classpath = join("." :: ((libraries >>= {recChildren(_, "jar")}) ∘ {_.getAbsolutePath}), ":")
            val files = join(recChildren(source, "scala") ∘ {_.getAbsolutePath}, " ")
            s"scalac -cp $classpath -d ${target.toString} $files"
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
}