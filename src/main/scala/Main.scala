import java.io.File
import java.nio.file.Files

import compilers._

object Main extends App {
	
	val comp = Compile (Scala, new File ("test/test.zip"))
	val res = compile (comp, l => new File (""), println)
	val compiled = new File ("compiled.zip")
	compiled delete ()
	Files move (res.get.getFile.toPath, compiled.toPath)
	
	
	/*
	private def listen (message: Message, write: String => Unit): Unit = {
		
	}
	
	Future {
		while (true) {
			Try {
				DataClient connect("worker", 8003)
			} recover {
				case t =>
					println ("Data Client failed!!!!!!")
					t printStackTrace ()
					Thread sleep 5000
			}
		}
	}
	
	while (true) {
		Try {
			(WorkerClient connect("worker", 8002)) (listen)
			Thread.sleep (5000)
		} recover {
			case t =>
				println ("Worker Client failed!!!!!!")
				t printStackTrace ()
				Thread sleep 5000
		}
	}
	*/
	
}