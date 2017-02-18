import java.io._
import java.nio.file.Files
import java.util.Properties
import java.util.stream.{Collectors, Stream}

import compilers.{Language, Library}
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.model.ZipParameters

import scalaz._
import Scalaz._
import scala.collection.JavaConverters._
import scala.collection.{Traversable, mutable}
import scala.concurrent.{Future, Promise}

package object util {
	
	def recChildren(file: File, ending: String): List[File] =
		if(file isDirectory)
			for (child <- file.listFiles.toList; file <- recChildren (child, ending)) yield file
		else if(fileEnding (file) == ending)
			file :: nil
		else
			nil
	
	def fileEnding(file: File): String = {
		val split = file.getName split "\\."
		if (split.length > 1)
			split(split.length - 1) toLowerCase
		else
			""
	}
	
	def streamToList[T](stream: Stream[T]): List[T] = (stream collect Collectors.toList()).asScala.toList
	
	def join(traversable: Traversable[String], s: String): String =
		if (traversable.isEmpty)
			""
		else
			traversable reduce (_ + s + _)
	
	def extract(zip: ZipFile): File = {
		val tmp = Files createTempDirectory "turnierserver-compiler-source"
		zip extractAll tmp.toFile.getAbsolutePath
		tmp.toFile
	}
	
	def zip(name: String, source: File): ZipFile = {
		val tmp = (Files createTempFile (name, ".zip")).toFile
		tmp delete
		val zip = new ZipFile(tmp)
		val params = new ZipParameters()
		params setIncludeRootFolder false
		zip createZipFileFromFolder (source, params, false, 0)
		zip
	}
	
	def readProperties(file: File): mutable.Map[String, String] = {
		val props = new Properties()
		props load new FileInputStream(file)
		props.asScala
	}
	
	def getStackTrace(t: Throwable): String = {
		val sw = new StringWriter()
		val pw = new PrintWriter(sw)
		t printStackTrace pw
		sw toString
	}
	
	def readLibraries (file: File, lang: Language): List[Library] = streamToList (Files lines file.toPath) ∘ { _.split ("/") } >>= { a =>
		if (a.length != 2) Nil
		else List (Library (lang, a (0), a (1)))
	}
	
	def wrap(string: String): String = '"' + string + '"'
	
}
