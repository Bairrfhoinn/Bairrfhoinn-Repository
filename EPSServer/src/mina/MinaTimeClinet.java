package mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import util.EPSConstant;

public class MinaTimeClinet {
	public static void main(String[] args) {
		IoConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName(EPSConstant.SOCKET_ENCODING))));
		connector.setHandler(new ClientHandler("你好！\r\n 大家好！"));
		connector.connect(new InetSocketAddress("localhost", EPSConstant.MINA_TIME_SERVER_PORT));
		connector.dispose();
	}
}

class ClientHandler extends IoHandlerAdapter {

	private final String values;

	public ClientHandler(String values) {
		this.values = values;
	}

	@Override
	public void sessionOpened(IoSession session) {
		session.write(values);
	}
}