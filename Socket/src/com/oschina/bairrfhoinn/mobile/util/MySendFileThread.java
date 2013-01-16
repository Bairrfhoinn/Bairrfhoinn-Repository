package com.oschina.bairrfhoinn.mobile.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MySendFileThread extends Thread {
	static final String ENCODING = "UTF-8";
	private String username;
	private String ipname;
	private int port;
	private byte byteBuffer[] = new byte[1024];
	private OutputStream outsocket;	
	private ByteArrayOutputStream myoutputstream;
	
	public MySendFileThread(ByteArrayOutputStream out, String username, String ipname, int port){
		this.username = username;
		this.ipname = ipname;
		this.port = port;
		this.myoutputstream = out;
		try{
			myoutputstream.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void run() {
        try{
        	//将图像数据通过Socket发送出去
            Socket tempSocket = new Socket(ipname, port);
            outsocket = tempSocket.getOutputStream();
        	String msg=java.net.URLEncoder.encode("PHONEVIDEO|"+username+"|", ENCODING);
            byte[] buffer= msg.getBytes();
            outsocket.write(buffer);            
            ByteArrayInputStream inputstream = new ByteArrayInputStream(myoutputstream.toByteArray());
            int amount;
            while ((amount = inputstream.read(byteBuffer)) != -1) {
                outsocket.write(byteBuffer, 0, amount);
            }
            myoutputstream.flush();
            myoutputstream.close();
            tempSocket.close();                   
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}