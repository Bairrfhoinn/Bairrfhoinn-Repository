package bairrfhoinn;

public class Consumer implements Runnable {
	
	Queue queue;

	@Override
	public void run() {
		while(true){
			queue.get();
		}
	}
	
	public Consumer(Queue queue){
		this.queue = queue;
		new Thread(this, "Consumer").start();
	}
}
