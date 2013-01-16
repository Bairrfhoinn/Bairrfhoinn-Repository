package com.mingluck.main;

import java.io.IOException;

public class ClientThread extends Thread {

	SmsClient client = new SmsClient();
	@Override
	public void run() {
		super.run();
		try {
			client.init();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			client.destroy();
		}
	}	
}