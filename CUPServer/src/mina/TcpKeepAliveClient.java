//package mina;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//import com.mingluck.bdbm.communication.MessageConstants;
//import com.mingluck.bdbm.exception.CommunicationException;
//
//public class TcpKeepAliveClient {
//
//	private String ip;
//
//	private int port;
//
//	private static Socket socket = null;
//
//	private static int timeout = 50 * 1000;
//
//	public TcpKeepAliveClient(String ip, int port) {
//
//		this.ip = ip;
//
//		this.port = port;
//
//	}
//
//	public void receiveAndSend() throws IOException {
//
//		InputStream input = null;
//
//		OutputStream output = null;
//
//		try {
//
//			if (socket == null || socket.isClosed() || !socket.isConnected()) {
//
//				socket = new Socket();
//
//				InetSocketAddress addr = new InetSocketAddress(ip, port);
//
//				socket.connect(addr, timeout);
//
////				socket.setSoTimeout(timeout);
//
//				System.out.println("TcpKeepAliveClientnew ");
//
//			}
//
//			input = socket.getInputStream();
//
//			output = socket.getOutputStream();
//
//			// read body
//
//			byte[] receiveBytes = {};// 收到的包字节数组
//
//			// if (input.available() > 0) {
//
//			receiveBytes = new byte[input.available()];
//
//			input.read(receiveBytes);
//
//			// send
//
//			System.out.println("TcpKeepAliveClientsend date :"
//					+ new String(receiveBytes));
//
//			output.write(receiveBytes, 0, receiveBytes.length);
//
//			output.flush();
//			byte[] cbuff = new byte[MessageConstants.MSG_SIZE_BIT];// 报文总长度
//			int len = input.read(cbuff);
//			String strLen = new String(cbuff);
//			try {
//				len = Integer.valueOf(strLen);
//			} catch (Exception e) {
//				throw new CommunicationException(
//						"Message format error, the first 8 bit must be integer");
//			}
//			cbuff = new byte[len];
//			input.read(cbuff);
//			String recvString = new String(cbuff);
//			System.out.println("接收回复报文成功:" + recvString);
//			// }
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			System.out.println("TcpClientnew socket error");
//
//		}
//
//	}
//
//	public static void main(String[] args) throws Exception {
//
//		TcpKeepAliveClient client = new TcpKeepAliveClient("127.0.0.1", 8002);
//
//		client.receiveAndSend();
//
//	}
//
//}