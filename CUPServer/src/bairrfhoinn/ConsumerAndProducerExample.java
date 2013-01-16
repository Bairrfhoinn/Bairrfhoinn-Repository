package bairrfhoinn;

public class ConsumerAndProducerExample {
	//使用多线程同步实现消费者生产者的样例
	public static void main(String[] args) {
		Queue queue = new Queue();
		new Producer(queue);
		new Consumer(queue);
		System.out.println("按Ctrl+C键以退出运行...");
	}
}