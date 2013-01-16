package com.oschina.bairrfhoinn;

import java.util.Date;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class TimeServerHandler extends IoHandlerAdapter {

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		//super.sessionIdle(session, status);
		System.out.println( "IDLE " + session.getIdleCount(status));
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String str = message.toString();
		if("quit".equalsIgnoreCase(str.trim())){
			session.close();
			return;
		}
		Date date = new Date();
		session.write(date.toString());
		System.out.println("Message written...");
	}
}