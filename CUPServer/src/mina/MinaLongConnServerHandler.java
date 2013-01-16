package mina;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import util.BytesUtils;
import util.CommonUtils;

public class MinaLongConnServerHandler extends IoHandlerAdapter {

	static String fileName = "src/6228481982165798016cups";

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
		String addr = ((InetSocketAddress) session.getRemoteAddress())
				.getAddress().getHostAddress();
		int port = ((InetSocketAddress) session.getRemoteAddress()).getPort();
		System.out.println("sessionCreated, IP:" + addr + ", PORT:" + port
				+ ",sessionid:" + session.getId());
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		try {
			super.messageSent(session, message);
			System.out.println(">>>>server:send message is:"
					+ CommonUtils.getString(message));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		try {
			super.messageReceived(session, message);
			Thread.sleep(5000);
			IoBuffer buffer = (IoBuffer) message;
			byte[] in = new byte[buffer.limit()];
			buffer.get(in);
			if (ClientSocket.getInstance().getClient() != null && in.length > 10) {
				//TODO: hanmiao 仅供测试，上线前需要先删除此代码
				in = FileUtils.readFileToByteArray(new File("src/message/Cps2CupAtmInquireReq"));
				//取得第32域报文中的四位标识码
				String aiic = CommonUtils.getAiic(in);
				String suffix = CommonUtils.getFileNameFromMsg(in);
				String filename = new StringBuffer("src/Cup2Cps").append(suffix).toString();
				byte[] out = FileUtils.readFileToByteArray(new File(filename));
				out = BytesUtils.updateMacAndBlocks(in, out);
				int len = out.length - 1;
				out = Arrays.copyOf(out, len);
				if("0301".equals(aiic)){
					//该报文的类型是本代他，返回报文
					ClientSocket.getInstance().getClient().send(out);
				}
			}
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
			cause.printStackTrace();
			super.exceptionCaught(session, cause);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("server error==============");
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		System.out.println("server sessionClosed==============");
	}
}