
object Main extends App {
	
	WorkerClient listen { message =>
		println("received message: " + message)
	}
	
	while(true) WorkerClient connect ("worker", 8002)
	
}
