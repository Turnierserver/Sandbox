import java.io.{File, FileInputStream}
import java.nio.file.Files
import java.util.Properties
import java.util.stream.{Collectors, Stream}

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters
import scalaz._, Scalaz._

import scala.collection.JavaConverters._
import scala.collection.{Traversable, mutable}

package object util {
	
	def recChildren(file: File, ending: String): List[File] =
		for(f <- streamToList(Files walk file.toPath) ∘ {_.toFile} if fileEnding(f) equalsIgnoreCase ending) yield f
	
	def fileEnding(file: File): String = {
		val split = file.getName split "."
		split(split.length - 1)
	}
	
	def streamToList[T](stream: Stream[T]): List[T] = (stream collect Collectors.toList()).asScala.toList
	
	def join(traversable: Traversable[String], s: String): String = traversable reduce (_ + s + _)
	
	def extract(zip: ZipFile): File = {
		val tmp = Files createTempDirectory "turnierserver-compiler-source"
		zip extractAll tmp.toFile.getAbsolutePath
		tmp.toFile
	}
	
	def zip(source: File): ZipFile = {
		val tmp = (Files createTempFile ("turnierserver-compiler-result", "zip")).toFile
		val zip = new ZipFile(tmp)
		val params = new ZipParameters()
		zip createZipFileFromFolder (source, params, false, 0)
		zip
	}
	
	def readProperties(file: File): mutable.Map[String, String] = {
		val props = new Properties()
		props load new FileInputStream(file)
		props.asScala
	}
	
}
