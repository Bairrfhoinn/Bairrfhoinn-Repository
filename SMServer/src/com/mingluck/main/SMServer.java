package com.mingluck.main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class SMServer {

	IoAcceptor acceptor;
	public static final int PORT = 9123;
	
	public void init() throws IOException{
		acceptor = new NioSocketAcceptor(10);
		acceptor.getFilterChain().addLast("Logger", new LoggingFilter());
		acceptor.setHandler(new SmServerHandler());
		acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool()));
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));
	}
	
	public void destory(){
		acceptor.dispose();
	}
	
	//短信平台服务器端
	public static void main(String[] args) throws IOException {
		SMServer server = new SMServer();
		server.init();
		System.out.println("server starts, listening to the port " + PORT);
	}
}