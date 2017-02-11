import java.io.{File, FileInputStream}
import java.nio.file.Files
import java.util.Properties
import net.lingala.zip4j.core._
import scala.sys.process._
import scala.collection._
import scala.collection.JavaConverters._
import util._

package object compilers {
	
    case class Compile(lang: Language, zip: File)
    
    sealed trait Language
    case class Java() extends Language
    case class Scala() extends Language
    case class Cpp() extends Language
    case class Python() extends Language
    case class Rust() extends Language
    case class Go() extends Language
    case class Haskell() extends Language
    
    case class Library(lang: Language, name: String, version: String)
    
    
    def compile(comp: Compile, fetchLibrary: Library => File, logger: String => Unit): Option[ZipFile] = {
        val folder = extract(new ZipFile(comp.zip.getAbsolutePath))
        val propertiesFile = new File(folder, "properties.prop")
        val librariesFile = new File(folder, "properties.txt")
        if(!propertiesFile.exists) {
            logger("properties.prop existiert nicht!")
            return None
        }
        if(!librariesFile.exists) {
            logger("libraries.txt existiert nicht!")
            return None
        }
        val properties = readProperties(propertiesFile)
        val libraries = readLibraries(librariesFile, comp.lang) map fetchLibrary
        val target = Files.createTempDirectory("turnierserver-compiler-target").toFile
        val command = createCommand(comp.lang, properties, libraries, folder, target)
        if(command ! ProcessLogger(logger) == 0) {
	        Files.copy(propertiesFile.toPath, new File(target, "properties.prop").toPath)
	        Some(zip(target))
        }
        else None
    }
    
    private def readLibraries(file: File, lang: Language): List[Library] = streamToList(Files.lines(file.toPath)) map(_.split("/")) flatMap { a =>
        if(a.length != 2) Nil
        else List(Library(lang, a(0), a(1)))
    }

    private def createCommand(lang: Language, properties: mutable.Map[String, String], libraries: List[File], source: File, target: File): ProcessBuilder = lang match {
        case Java() =>
            val classpath = join("." :: (libraries flatMap (recChildren(_, "jar")) map (_.getAbsolutePath)), ":")
            val files = join(recChildren(source, "java") map (_.getAbsolutePath), " ")
            s"javac -cp $classpath -d ${target.toString} $files"
        case Scala() =>
            val classpath = join("." :: (libraries flatMap (recChildren(_, "jar")) map (_.getAbsolutePath)), ":")
            val files = join(recChildren(source, "scala") map (_.getAbsolutePath), " ")
            s"scalac -cp $classpath -d ${target.toString} $files"
        case Cpp() =>
            "echo 'aaay cpp'"
        case Python() =>
            "echo 'aaay python'"
        case Rust() =>
            "echo 'aaay rust'"
        case Go() =>
            "echo 'aaay go'"
        case Haskell() =>
            "echo 'aaay haskell'"
    }
}