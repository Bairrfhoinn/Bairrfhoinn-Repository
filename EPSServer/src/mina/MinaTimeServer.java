package mina;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.firewall.BlacklistFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import util.EPSConstant;

public class MinaTimeServer {
	public static void main(String[] args) throws IOException {

		IoAcceptor acceptor = new NioSocketAcceptor();
		BlacklistFilter blacklistFilter = new BlacklistFilter();
		InetAddress[] address = new InetAddress[1];
		address[0] = InetAddress.getByName(EPSConstant.MINA_TIME_SERVER_IP);
		blacklistFilter.setBlacklist(address);
		acceptor.getFilterChain().addFirst("black", blacklistFilter);
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("GBK"))));
		acceptor.setHandler(new TimeServerHandler());
		acceptor.getSessionConfig().setReadBufferSize(2048);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(EPSConstant.MINA_TIME_SERVER_PORT));
		System.out.println("MINA Time Server started,bind port:" + EPSConstant.MINA_TIME_SERVER_PORT);
	}
}

class TimeServerHandler extends IoHandlerAdapter {
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {

		String str = message.toString();
		System.out.println("str:" + str);
		if (str.trim().equalsIgnoreCase("quit")) {
			session.close(true);
			return;
		}
		Date date = new Date();
		session.write(date.toString());
		System.out.println("Message written...");
		session.close(true);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		System.out.println("IDLE " + session.getIdleCount(status));
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}
}