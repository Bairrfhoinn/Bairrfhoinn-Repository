package bairrfhoinn;

public class Queue {
	int n;
	boolean valueSet = false;
	
	synchronized int get(){
		if(!valueSet){
			try{
				wait();
			}catch(InterruptedException e){
				System.out.println("在Queue.get()方法中捕获到中断例外...");
			}
		}
		System.out.println("获取到：" + n);
		valueSet = false;
		notify();		
		return n;
	}
	
	synchronized void put(int n){
		if(valueSet){
			try{
				wait();
			}catch(InterruptedException e){
				System.out.println("在Queue.put()方法中捕获到中断例外...");
			}
		}
		this.n = n;
		valueSet = true;
		System.out.println("生产出：" + n);
		notify();
	}
}
