package example;

import java.util.Timer;
import java.util.TimerTask;

public class SendMessageReminder {

	Timer timer;
	public static final int ONE_MINUTES = 1;
	
	public SendMessageReminder(int seconds){
		timer = new Timer();
		//启动定时任务，每秒执行壹次
		timer.schedule(new RemindTask(), 0, seconds * 1000);
	}
	
	public static void main(String[] args) {
		System.out.println("About to schedule task.");
		new SendMessageReminder(ONE_MINUTES);
		System.out.println("Task scheduled.");
	}
}

class RemindTask extends TimerTask{
	int i = 0;
	public void run(){
		System.out.println("send a short message to the server: " + i++);
	}
}