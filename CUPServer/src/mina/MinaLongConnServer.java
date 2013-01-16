package mina;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import util.ByteContentCodecFactory;

public class MinaLongConnServer {
	public MinaLongConnServer(int port) {
		this.port = port;
	}

	private int port;
	public IoAcceptor acceptor = null;

	public void start() throws IOException {
		acceptor = new NioSocketAcceptor(5);
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("threadPool",
				new ExecutorFilter(Executors.newSingleThreadExecutor()));		
		acceptor.getFilterChain().addLast("codec",
						new ProtocolCodecFilter(new ByteContentCodecFactory()));
		acceptor.setHandler(new MinaLongConnServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.bind(new InetSocketAddress(port));
		
		System.out.println("Listening on port " + port);

	}

	public static void main(String[] args) throws IOException {
		MinaLongConnServer server = new MinaLongConnServer(59005);
		if (server.acceptor == null) {
			server.start();
		}
	}
}