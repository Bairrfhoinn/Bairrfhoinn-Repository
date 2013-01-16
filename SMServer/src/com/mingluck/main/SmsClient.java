package com.mingluck.main;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Timer;

import org.apache.commons.io.FileUtils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

public class SmsClient {
	
	private static final int PORT = 9123;
	public  static String address = "127.0.0.1";
	private static InetSocketAddress socketAddres = new InetSocketAddress(address, PORT);	
	private NioSocketConnector connector = null; 
	
	public void init() throws IOException{
		connector = new NioSocketConnector();
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setConnectTimeoutMillis(3000);
		connector.setHandler(new SmsClientHandler());//设置事件处理器 
		ConnectFuture cf = connector.connect(socketAddres);//建立连接
		cf.awaitUninterruptibly();
		
		byte[] message = FileUtils.readFileToByteArray(new File("src/input.xml"));
		IoSession session = cf.getSession();
		Timer timer = new Timer();
		timer.schedule(new SendMessageTask(message, session), 0, 3000);
		
		cf.getSession().getCloseFuture().awaitUninterruptibly();//等待连接断开		
	}

	public void destroy(){
		socketAddres = null;
		connector.dispose();
	}

	public static void main(String[] args) {		
		SmsClient client = new SmsClient();
		try {
			client.init();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			client.destroy();
		}
	}	
}