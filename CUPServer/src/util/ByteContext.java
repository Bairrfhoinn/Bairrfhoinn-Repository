/**
 * Copyright @2010 Shanghai MingLuck Co. Ltd. * All right reserved.
 */
package util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * FileName: ByteContext.java Description: This Class used for BDBM
 * 
 * @Company: Shanghai MingLuck Co. Ltd. Developer: David.wang,
 * @Date: 2010-10-15 Modifier: NONE,
 * @Date: NONE
 * @Version: V1.0
 * 
 */
public class ByteContext implements Serializable{
	private static final long serialVersionUID = 1L;
	static Logger log =  Logger.getLogger(ByteContext.class);
	private ByteArrayOutputStream buf;
	private int receivedLen = 0;
	private int msgLength = 0;

	/**
	 * @param charset
	 */
	public ByteContext() {
		buf = new ByteArrayOutputStream();
	}

	/**
	 * 
	 * 
	 * @return IoBuffer
	 */
	public byte[] getBuffer() {
		return buf.toByteArray();
	}

	/**
	 * 
	 * 
	 * @return matchCount
	 */
	public int getMsgLength() {
		return msgLength;
	}

	/**
	 * 
	 * 
	 * @param matchCount
	 *            报文长度
	 */
	public synchronized void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}
	
	/**
	 * @param n
	 * @return
	 */
	public synchronized int readMsgLength(int n) throws NumberFormatException{
		if(this.receivedLen < n){
			return -1;
		}
		
		if(this.msgLength > 0){
			return this.msgLength;
		}
		
		byte[] byteRecv = this.buf.toByteArray();
		byte[] byteTmp = new byte[n];
		System.arraycopy(byteRecv, 0, byteTmp, 0, n);
		
		String strLen = new String(byteTmp);
		this.msgLength = 0;
		try {
			this.msgLength = Integer.parseInt(strLen);
		} catch (Exception e) {
			log.error("Received FTS message", e);
			throw new NumberFormatException("Read message length error from FTS:" + e.getMessage());
		}
		
		//切掉头上表示长度的8个字节
		byteTmp = new byte[this.receivedLen-n];
		System.arraycopy(byteRecv, n, byteTmp, 0, this.receivedLen-n);
		this.buf.reset();
		try {
			this.buf.write(byteTmp);
		} catch (IOException e) {
			log.error(e);
			return -1;
		}
		
		this.receivedLen -= n;//接收长度调整为正确值
		
		return this.msgLength;
	}
	
	/**
	 * @return
	 */
	public synchronized int getReceivedLength(){
		return this.receivedLen;
	}

	/**
	 * 
	 * 
	 */
	public synchronized void reset() {
		try {
			this.buf.close();
			this.buf = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.msgLength = 0;
		this.receivedLen = 0;
	}

	/**
	 * 
	 * @param in
	 *            输入流
	 */
	public synchronized void append(IoBuffer in) {
		int len = in.remaining();
		if(len<=0)
			return;
		receivedLen += len;
		
		if(null == this.buf){
			this.buf = new ByteArrayOutputStream();
		}
		
		byte[] b = new byte[len];
		in.get(b);
		try {
			this.buf.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
