package com.mingluck.main;

import java.util.TimerTask;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class SendMessageTask extends TimerTask {
	
	byte[] message;
	IoSession session;
	
	public SendMessageTask(byte[] message, IoSession session){
		this.message = message;
		this.session = session;
	}
	
	@Override
	public void run(){
		System.out.println("���͹�ȥ���ַ������鳤��Ϊ��" + message.length);
		session.write(IoBuffer.wrap(message));
	}
}
