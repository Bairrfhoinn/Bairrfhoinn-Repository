package com.oschina.bairrfhoinn;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.example.imagine.step1.server.ImageServerIoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class ImageServer {
	public static final int PORT = 33789;
	
	public static void main(String[] arguments) throws IOException{
		ImageServerIoHandler handler = new ImageServerIoHandler();
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.getFilterChain().addLast("protocol", new ProtocolCodecFilter(new ImageCodecFactory(false)));
        acceptor.setDefaultLocalAddress(new InetSocketAddress(PORT));
        acceptor.setHandler(handler);
        acceptor.bind();
        System.out.println("server is listenig at port " + PORT);
	}
}