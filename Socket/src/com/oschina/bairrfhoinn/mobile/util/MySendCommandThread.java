package com.oschina.bairrfhoinn.mobile.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MySendCommandThread extends Thread {
	private String command;
	public static final String serverUrl = "127.0.0.1";
	public static final int serverPort = 8888;
	
	public MySendCommandThread(String command){
		this.command = command;
	}
	
	public void run(){
		try{
			Socket socket = new Socket(serverUrl, serverPort);
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			out.println(command);
			out.flush();
			socket.close();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
