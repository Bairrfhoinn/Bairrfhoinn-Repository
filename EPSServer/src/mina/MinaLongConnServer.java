package mina;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaLongConnServer {
	public MinaLongConnServer(int port) {
		this.port = port;
	}

	private int port;
	public IoAcceptor acceptor = null;

	public void start() throws IOException {
		acceptor = new NioSocketAcceptor(5);
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setHandler(new MinaLongConnServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.bind(new InetSocketAddress(port));
		System.out.println("Listeningon port " + port);
	}

	public static void main(String[] args) throws IOException {
		MinaLongConnServer server = new MinaLongConnServer(59005);
		if (server.acceptor == null) {
			server.start();
		}
	}
}