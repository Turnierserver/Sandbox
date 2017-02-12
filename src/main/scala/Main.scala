import scala.concurrent.Future
import scala.util.Try

object Main extends App {
	
	private def listen(message: Message, write: String => Unit): Unit = {
		
	}
	
	Future {
		while(true) {
			Try {
				DataClient connect ("worker", 8003)
			} recover {
				case t =>
					println("Data Client failed!!!!!!")
					t printStackTrace ()
			}
		}
	}
	
	while(true) {
		Try {
			(WorkerClient connect ("worker", 8002)) (listen)
			Thread.sleep(5000)
		} recover {
			case t =>
				println("Worker Client failed!!!!!!")
				t printStackTrace ()
		}
	}
	
}
