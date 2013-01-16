/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package mina;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import util.CommonUtils;
import util.EPSConstant;

import com.sun.jmx.snmp.daemon.CommunicationException;

/**
 * FileName: ChatClientSupport.java Description: 同步消息处理客户端类，适合与FTS建立长连接的处理方式
 * 
 * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang, @Date: 2010-9-25
 *           Modifier: NONE, @Date: NONE
 * @Version: V1.0
 * 
 */
public class UnsyncClientSupport {
	static Logger log = Logger.getLogger(UnsyncClientSupport.class);
	private static final int CONNECT_TIMEOUT = 3600; // seconds
	NioSocketConnector connector = null;
	IoSession session = null;
	private String hostname;
	private int port;
	private Timer timer;
	
	public UnsyncClientSupport(String host, int port) {
		this.hostname = host;
		this.port = port;
	}

	/**
	 * 与FTS服务端节点建立异步连接
	 * 
	 * @return
	 * @throws CommunicationException
	 * @throws InterruptedException
	 */
	public IoSession connect() throws InterruptedException {
		timer = new Timer();
		if (null != this.session && !session.isConnected()) {
			//如果连接已经建立，则无需再次连接
			return this.session;
		}
		if (connector != null) {
			connector.connect();
			return this.session;
		}
		connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
		connector.getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newSingleThreadExecutor()));
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.setHandler(new ClientMsgReceiveHandle());// 设置消息处理对象句柄
		try {
			connector.setDefaultRemoteAddress(new InetSocketAddress(
					EPSConstant.UNSYNC_CLIENT_IP, EPSConstant.UNSYNC_CLIENT_PORT));
			ConnectFuture future = connector.connect(new InetSocketAddress(
					this.hostname, this.port));
			future.awaitUninterruptibly();
			IoSession session = future.getSession();
			this.setSession(session);
			//每分钟发送四個零以保持会话处于活跃状态
			timer = new Timer();
			timer.schedule(new HoldConnectionTask(), 0, 60000);
		} catch (RuntimeIoException e) {
			e.printStackTrace();
		}
		return session;
	}

	public IoSession getSession() {
		return session;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	/**
	 * 同步发送消息到指定FTS节点
	 * 
	 * @param node
	 * @param message
	 * @throws CommunicationException
	 * @throws InterruptedException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void send(String msg) throws InterruptedException, IOException {
		// EPS 发送到 CPS 的报文，每秒发送壹千条消息
		byte[] message;
		List messages = new ArrayList();
		try {
			message = FileUtils.readFileToByteArray(new File("src/Eps2CpsAtmInquireReq"));
			message = Arrays.copyOf(message, message.length-1);
			messages.add(message);
			
			message = FileUtils.readFileToByteArray(new File("src/Eps2CpsAuthCancelReq"));
			message = Arrays.copyOf(message, message.length-1);
			messages.add(message);
			
			message = FileUtils.readFileToByteArray(new File("src/Eps2CpsAuthReq"));
			message = Arrays.copyOf(message, message.length-1);
			messages.add(message);
			
			message = FileUtils.readFileToByteArray(new File("src/Eps2CpsPosInquireReq"));
			message = Arrays.copyOf(message, message.length-1);
			messages.add(message);
			
			message = FileUtils.readFileToByteArray(new File("src/Eps2CpsWithdrawReq"));
			message = Arrays.copyOf(message, message.length-1);
			messages.add(message);
			
			for(int i=0; i<messages.size(); i++){
				byte[] tmp = (byte[])messages.get(i);
				byte[] data = CommonUtils.update(tmp, false);
				messages.set(i, data);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		timer = new Timer();
		timer.schedule(new SendMessageTask(messages), 0, 1000);
	}

	public void stop(){
		timer.cancel();
	}
	/**
	 * 关闭客户端的网络连接
	 */
	public void close() {
		if (null != session && session.isConnected()) {
			session.close(true);
		}
		if (null != connector) {
			connector.dispose();
		}
	}

	/**
	 * 返回四位长度的数字字符串，'0'补足前空位
	 * 
	 * @param msg
	 * @return
	 */
	public String getMessageStrLen(String msg) {
		int len = msg.toString().getBytes().length;
		String slen = String.valueOf(len);
		int n = slen.toCharArray().length;
		char[] c = { '0', '0', '0', '0' };
		for (int i = 0; i < n; ++i) {
			c[4 - i - 1] = slen.toCharArray()[n - i - 1];
		}
		slen = new String(c);
		return slen;
	}

	//定时器类，定时发送消息到服务器
	class HoldConnectionTask extends TimerTask {
		byte[] msg ="0000".getBytes();
		@Override
		public void run() {
			getSession().write(IoBuffer.wrap(msg));
		}
	}
	
	//定时器类，定时发送壹千条消息到服务器
	class SendMessageTask extends TimerTask {
		@SuppressWarnings("rawtypes")
		public List sendMessages;
		@SuppressWarnings("rawtypes")
		public SendMessageTask(List sendMessages){
			this.sendMessages = sendMessages;
		}
		
		//五种消息，每种发送200次
		static final int COUNT = 200;
		int k = 1;
		@Override
		public void run(){
			for(int i=0; i<sendMessages.size(); i++){
				byte[] msg = (byte[])sendMessages.get(i);
				for(int j=0; j < COUNT; j++){
					byte[] temp = CommonUtils.update(msg, false);
					ClinetSocket.getInstance().getClient().getSession().write(IoBuffer.wrap(temp));
				}
				System.out.println("========>k=" + k++);
			}
		}
	}
	
	public static void main(String[] arguments) throws IOException {
		// 将报文内容读入 byte[] 数组 array
		byte[] array0 = FileUtils.readFileToByteArray(new File(
				"src/Eps2CpsAtmInquireReq"));
		CommonUtils.update(array0, false);
		byte[] array1 = FileUtils.readFileToByteArray(new File(
				"src/Eps2CpsAuthCancelReq"));
		CommonUtils.update(array1, true);
		byte[] array2 = FileUtils.readFileToByteArray(new File(
				"src/Eps2CpsAuthReq"));
		CommonUtils.update(array2, true);
		byte[] array3 = FileUtils.readFileToByteArray(new File(
				"src/Eps2CpsPosInquireReq"));
		CommonUtils.update(array3, false);
		byte[] array4 = FileUtils.readFileToByteArray(new File(
				"src/Eps2CpsWithdrawReq"));
		CommonUtils.update(array4, false);
	}
}