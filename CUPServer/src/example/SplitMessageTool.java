package example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import util.CommonUtils;

public class SplitMessageTool {
	/*
	 * 自动分析各报文域的信息并打印至控制台
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {
//		String filename = "src/Cup2Cps011003000006"; //src/message/Cps2CupAtmInquireReq";
//		boolean isHeaderExists = true;
		String filename = "src/message/Eps2CpsWithdrawReq";
		boolean isHeaderExists = false;
//		String filename = "src/message/Cup2CpsWithdrawRsp";
//		boolean isHeaderExists = true;
		//start用于计算相对起始位置的偏移量
		int start = 0;
		byte[] message = FileUtils.readFileToByteArray(new File(filename));
		byte[] messageLength = Arrays.copyOfRange(message, start, start + 4);
		System.out.print("消息长度(单位：字节)：");
		print(messageLength);
		start = start + 4;
		if(isHeaderExists){
			byte[] messageHeader = Arrays.copyOfRange(message, start, start + 46);
			System.out.print("报文域0(消息头)：");
			print(messageHeader);
			start = start + 46;
		}
		byte[] messageType = Arrays.copyOfRange(message, start, start + 4);
		System.out.print("报文类型标识符：");
		print(messageType);
		start = start + 4;
		byte[] bitmap = Arrays.copyOfRange(message, start, start + 16);
		System.out.println("位图信息:" + CommonUtils.byteToBinaryString(bitmap));
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		System.out.print("报文域1(所有存在的报文域)：");
		for(int i=0; i<list.size(); i++){
			System.out.print(list.get(i) + " ");
		}
		System.out.println();
		start = start + 16;
		int[] offset = CommonUtils.getAllOffsetValues();
		if(list.contains(2)){
			byte[] temp = Arrays.copyOfRange(message, start, start + 2);
			offset[2] = Integer.valueOf(new String(temp)) + 2;
			byte[] primaryAccount = Arrays.copyOfRange(message, start, start + offset[2]);
			System.out.print("报文域2(主帐号)：");
			print(primaryAccount);
			start = start + offset[2];
		}
		for(int i=3; i<=28; i++){
			if(list.contains(i)){
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i +":");
				print(msg);
				start = start + offset[i];
			}
		}
		for(int i=32; i<=36; i++){
			if(list.contains(i) && i !=36){
				//该报文域的长度不定，用前两個字节表示长度值，先获取其长度值
				byte[] temp = Arrays.copyOfRange(message, start, start + 2);
				offset[i] = Integer.valueOf(new String(temp)) + 2;
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i +":");
				print(msg);
				start = start + offset[i];
			}
			
			if(list.contains(36) && i==36){
				//该报文域的长度不定，用前三個字节表示长度值，先获取其长度值
				byte[] temp = Arrays.copyOfRange(message, start, start + 3);
				offset[i] = Integer.valueOf(new String(temp)) + 3;
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i +":");
				print(msg);
				start = start + offset[36];
			}
		}
		for(int i=37; i<=43; i++){
			if(list.contains(i)){
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i +":");
				print(msg);
				start = start + offset[i];
			}
		}
		//解析报文域44、45、48，分别计算他们的报文长度
		if(list.contains(44)){
			byte[] temp = Arrays.copyOfRange(message, start, start + 2);
			offset[44] = Integer.parseInt(new String(temp)) + 2;
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[44]);
			print(msg);
			start = start + offset[44];
		}
		if(list.contains(45)){
			byte[] temp = Arrays.copyOfRange(message, start, start + 2);
			offset[45] = Integer.parseInt(new String(temp)) + 2;
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[45]);
			print(msg);
			start = start + offset[45];
		}
		if(list.contains(48)){
			byte[] temp = Arrays.copyOfRange(message, start, start + 3);
			offset[48] = Integer.parseInt(new String(temp)) + 3;
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[48]);
			print(msg);
			start = start + offset[48];
		}
		for(int i=49; i<=51; i++){
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
			System.out.print("报文域" + i + ":");
			print(msg);
			start = start + offset[i];
		}
		if(list.contains(52)){
			byte[] data = Arrays.copyOfRange(message, start, start + offset[52]);
			//将字节数组转化成二进制字符串并输出
			System.out.println("报文域52(個人标识码数据): " + CommonUtils.byteToBinaryString(data));
			start = start + offset[52];
		}
		if(list.contains(53)){
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[53]);
			System.out.print("报文域53:");
			print(msg);
			start = start + offset[53];
		}
		if(list.contains(54)){
			byte[] temp = Arrays.copyOfRange(message, start, start + 3);
			offset[54] = Integer.parseInt(new String(temp)) + 3;
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[54]);
			System.out.print("报文域54:");
			print(msg);
			start = start + offset[54];
		}
		for(int i=55; i<=63; i++){
			if(list.contains(i)){
				byte[] temp = Arrays.copyOfRange(message, start, start + 3);
				offset[i] = Integer.parseInt(new String(temp)) + 3;
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i + ":");
				print(msg);
				start = start + offset[i];				
			}
		}
		for(int i=70; i<=96; i++){
			if(list.contains(i)){
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i + ":");
				print(msg);
				start = start + offset[i];
			}
		}
		for(int i=100; i<=103; i++){
			if(list.contains(i)){
				byte[] temp = Arrays.copyOfRange(message, start, start + 2);
				offset[i] = Integer.parseInt(new String(temp)) + 2;
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i + ":");
				print(msg);
				start = start + offset[i];
			}
		}
		for(int i=121; i<=123; i++){
			if(list.contains(i)){
				byte[] temp = Arrays.copyOfRange(message, start, start + 3);
				offset[i] = Integer.parseInt(new String(temp)) + 3;
				byte[] msg = Arrays.copyOfRange(message, start, start + offset[i]);
				System.out.print("报文域" + i + ":");
				print(msg);
				start = start + offset[i];				
			}
		}
		if(list.contains(128)){
			byte[] msg = Arrays.copyOfRange(message, start, start + offset[128]);
			//将字节数组转化成二进制字符串并输出
			System.out.println("报文域128(报文鉴别码)：" + CommonUtils.byteToBinaryString(msg));
			start = start + offset[128];
		}
	}

	private static void print(byte[] message) {
		int length = message.length;
		boolean printable = true;
		for(int i=0; i<length; i++){
			System.out.print(message[i] + " ");
			printable = printable && isAlphaOrDigital(message[i]);
		}
		if(printable){
			System.out.print("====>");
			for(int i=0; i<length; i++){
				System.out.print((message[i] - 48) + " ");
			}
		}
		System.out.println();
	}

	private static boolean isAlphaOrDigital(byte b) {
		return (b>=48 && b<=57) || (b>=65 && b<=90) || (b>=97 && b<=122);
	}
	
	private static String toString(byte[] message){
		String aa = new String("");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<message.length; i++){
			if(message[i]>=48 && message[i]<=57){
				//String str = Byte.toString(message[b] - 48);
				//sb.append(str);
			}
		}
		return "";
	}
}