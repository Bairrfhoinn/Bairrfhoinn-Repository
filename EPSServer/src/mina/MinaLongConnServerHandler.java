package mina;

import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import util.CommonUtils;

public class MinaLongConnServerHandler extends IoHandlerAdapter {

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		String addr = ((InetSocketAddress) session.getRemoteAddress())
				.getAddress().getHostAddress();
		int port = ((InetSocketAddress) session.getRemoteAddress()).getPort();
		System.out.println("sessionCreated, IP:" + addr + ", PORT:" + port
				+ ", sessionid:" + session.getId());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		try {
			super.messageSent(session, message);
			System.out.println(">>>>server:send message is:" + CommonUtils.getString(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		try {
			super.messageReceived(session, message);

			System.out.println("Messagereceived in the long connect server...");
			System.out.println(">>>>server:receive message is:" + CommonUtils.getString(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		try {
			super.sessionIdle(session, status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		try {
			super.exceptionCaught(session, cause);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("server error==============");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}
}