package sandbox

import java.io.File

import scala.sys.process._

object Isolate {
	
	def init (id: Int): Boolean = (s"/isolate/isolate --init -b $id" !) == 0
	
	def run (id: Int, ai: File, runCommand: String): Boolean = (s"/isolate/isolate --run -b $id --dir=${wrap (ai.getAbsolutePath) } ${wrap (runCommand) }" !) == 0
	
	private def wrap (s: String) = '"' + s + '"'
	
	def cleanup (id: Int): Boolean = (s"/isolate/isolate --cleanup -b $id" !) == 0
}
