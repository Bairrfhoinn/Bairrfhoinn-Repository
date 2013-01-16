///*
// *  Licensed to the Apache Software Foundation (ASF) under one
// *  or more contributor license agreements.  See the NOTICE file
// *  distributed with this work for additional information
// *  regarding copyright ownership.  The ASF licenses this file
// *  to you under the Apache License, Version 2.0 (the
// *  "License"); you may not use this file except in compliance
// *  with the License.  You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing,
// *  software distributed under the License is distributed on an
// *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// *  KIND, either express or implied.  See the License for the
// *  specific language governing permissions and limitations
// *  under the License.
// *
// */
//package mina;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//import java.util.concurrent.TimeUnit;
//
//import org.apache.log4j.Logger;
//import org.apache.mina.core.buffer.IoBuffer;
//import org.apache.mina.core.future.ConnectFuture;
//import org.apache.mina.core.future.ReadFuture;
//import org.apache.mina.core.session.IoSession;
//import org.apache.mina.filter.codec.ProtocolCodecFilter;
//import org.apache.mina.filter.logging.LoggingFilter;
//import org.apache.mina.transport.socket.SocketSessionConfig;
//import org.apache.mina.transport.socket.nio.NioSocketConnector;
//
//import com.mingluck.bdbm.communication.MessageBuilder;
//import com.mingluck.bdbm.communication.MessageConstants;
//import com.mingluck.bdbm.communication.codec.TextContentCodecFactory;
//import com.mingluck.bdbm.communication.message.MessageResponse;
//import com.mingluck.bdbm.exception.CommunicationException;
//
///**
// * FileName: ChatClientSupport.java Description: 同步消息处理客户端类
// * 
// * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang, @Date: 2010-9-25
// *           Modifier: NONE, @Date: NONE
// * @Version: V1.0
// * 
// */
//public class SyncClientSupport {
//	static Logger log = Logger.getLogger(SyncClientSupport.class);
//	private static final int CONNECT_TIMEOUT = 60; // seconds
//	private static final int RECEIVE_TIMEOUT = 30;// 接收返回的超时时间
//	private NioSocketConnector connector = null;
//	private IoSession session = null;
//
//	private String hostname;
//	private int port;
//
//	/**
//	 * 连接到服务端
//	 * 
//	 * @param name
//	 * @param handler
//	 */
//	public SyncClientSupport(String host, int port) {
//		this.hostname = host;
//		this.port = port;
//	}
//
//	/**
//	 * @param msg
//	 * @return
//	 * @throws CommunicationException
//	 */
//	public synchronized boolean send(String msg) throws CommunicationException {
//		log.info("下行报文:" + msg);
//		Socket socket = null;
//		InputStream in = null;
//		OutputStream pw = null;
//		String recvString = "";
//		try {
//			log.info("开始建立Socket连接：" + this.hostname + ":" + this.port);
//
//			// 建立连接
//			socket = new Socket(this.hostname, this.port);
//			socket.setSoTimeout(30000);
//			in = socket.getInputStream();
//			pw = socket.getOutputStream();
//
//			log.info("建立Socket连接成功:" + msg);
//			// 发送报文
//			String slen = MessageBuilder.getMessageStrLen(msg.length());
//			pw.write((slen + msg).getBytes(MessageConstants.MSG_CHARSET));
//			pw.flush();
//
//			log.info("发送报文成功:" + msg);
//
//			// 接收回复报文
//			byte[] cbuff = new byte[MessageConstants.MSG_SIZE_BIT];// 报文总长度
//			int len = in.read(cbuff);
//			String strLen = new String(cbuff);
//			try {
//				len = Integer.valueOf(strLen);
//			} catch (Exception e) {
//				throw new CommunicationException(
//						"Message format error, the first 8 bit must be integer");
//			}
//			cbuff = new byte[len];
//			in.read(cbuff);
//			recvString = new String(cbuff);
//			log.info("接收回复报文成功:" + recvString);
//		} catch (Exception e) {
//			log.info("下发报文异常:" + e);
//			log.error("Failed to connect FTS", e);
//			throw new CommunicationException("Failed to connect FTS"
//					+ e.getMessage());
//		} finally {
//			try {
//				if (in != null)
//					in.close();
//				if (pw != null)
//					pw.close();
//				if (socket != null)
//					socket.close();
//			} catch (Exception e2) {
//				log.error(e2);
//				return false;
//			}
//		}
//		// 如果回执消息为失败，则将发送消息判为失败
//		MessageResponse res = new MessageResponse(recvString);
//		if (!MessageConstants.MESSAGE_OK.equals(res.getErrorCode())) {
//			log.info("Message sent, but no response from FTS,ERRCODE:"
//					+ res.getErrorCode());
//			throw new CommunicationException(res.getErrorCode(), res
//					.getErrorMsg());
//		}
//		log.info("Message sent，and response ok from FTS");
//		return true;
//	}
//
//	/**
//	 * 同步发送消息到指定FTS节点
//	 * 
//	 * @param msg
//	 * @return
//	 * @throws CommunicationException
//	 */
//	public boolean sendByMima(String msg) throws CommunicationException {
//		connector = new NioSocketConnector();
//		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
//
//		connector.getFilterChain().addLast("codec",
//				new ProtocolCodecFilter(new TextContentCodecFactory()));
//		connector.getFilterChain().addLast("logger", new LoggingFilter());
//
//		SocketSessionConfig cfg = connector.getSessionConfig();
//		cfg.setUseReadOperation(true);// Enables operation.
//
//		ConnectFuture future = null;
//		try {
//			future = connector.connect(
//					new InetSocketAddress(this.hostname, this.port))
//					.awaitUninterruptibly();
//		} catch (Exception e) {
//			this.close();
//			throw new CommunicationException("Failed to connect FTS,HOSTNAME:"
//					+ hostname + ",PORT:" + port);
//		}
//		if (null == future || !future.isConnected()) {
//			this.close();
//			throw new CommunicationException("Failed to connect FTS,HOSTNAME:"
//					+ hostname + ",PORT:" + port);
//		}
//		future.awaitUninterruptibly();
//		session = future.getSession();
//		if (null != session && session.isConnected()) {
//			String slen = MessageBuilder.getMessageStrLen(msg.length());
//			IoBuffer buff = IoBuffer.wrap((slen + msg).getBytes());
//			session.write(buff).awaitUninterruptibly();
//			ReadFuture readFuture = session.read();// 接收
//			// Wait for the asynchronous operation to complete with 60s
//			if (readFuture.awaitUninterruptibly(RECEIVE_TIMEOUT,
//					TimeUnit.SECONDS)) {
//				MessageResponse res = new MessageResponse(readFuture
//						.getMessage().toString());
//				if (!MessageConstants.MESSAGE_OK.equals(res.getErrorCode())) {
//					this.close();
//					throw new CommunicationException(res.getErrorCode(), res
//							.getErrorMsg());
//				}
//			} else {
//				this.close();
//				throw new CommunicationException("No response from FTS");
//			}
//		}
//		this.close();
//
//		return true;
//	}
//
//	/**
//	 * 关闭客户端的网络连接
//	 */
//	public void close() {
//		if (null != session && session.isConnected()) {
//			session.close(true);
//		}
//		if (null != connector) {
//			connector.dispose();
//		}
//	}
//}
