package sandbox

import java.io.File

import compilers.Language

import scala.sys.process._

object Isolate {
	
	def init(id: Int): Boolean = (s"isolate --init -b $id" !) == 0
	
	def run(id: Int, ai: File, runCommand: String): Boolean = (s"isolate --run -b $id --dir=\"${ai.getAbsolutePath}\" \"$runCommand\"" !) == 0
	
	def cleanup(id: Int): Boolean = (s"isolate --cleanup -b $id" !) == 0
}
