import scala.util.continuations._

object Main extends App {
	
	reset {
		val message = shift(WorkerClient.listen)
		println("received message: " + message)
	}
	
	while(true) WorkerClient.connect("worker", 8001)
	
}
