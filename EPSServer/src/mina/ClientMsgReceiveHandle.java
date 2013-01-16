package mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import util.CommonUtils;

public class ClientMsgReceiveHandle extends IoHandlerAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache
	 * .mina.core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		System.out.println("client error==============");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache
	 * .mina.core.session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		try {
			super.messageReceived(session, message);
			System.out.println(">>>>client received message is:"
					+ CommonUtils.getString(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		try {
			super.messageSent(session, message);
//			System.out.println(">>>>client send message is:"
//					+ CommonUtils.getString(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionCreated(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}
}