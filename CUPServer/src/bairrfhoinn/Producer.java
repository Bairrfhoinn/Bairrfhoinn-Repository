package bairrfhoinn;

public class Producer implements Runnable {
	
	Queue queue;

	@Override
	public void run() {
		int i = 0;
		while(true){
			queue.put(i++);
		}
	}

	public Producer(Queue queue){
		this.queue = queue;
		new Thread(this, "Producer").start();
	}
}
