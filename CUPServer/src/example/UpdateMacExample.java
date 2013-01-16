package example;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import util.CommonUtils;
import util.JEncryption;

public class UpdateMacExample {	
	static String SOURCE_FILE_NAME = "src/message/Cps2CupAtmInquireReq";
	static String DESTINATION_FILE_NAME = "src/Cup2Cps011003000006";
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		//读取源报文信息，分析报文结构并提取出第7、11、12、13域的信息
		byte[] source = FileUtils.readFileToByteArray(new File(SOURCE_FILE_NAME));
		byte[] bitmap = Arrays.copyOfRange(source, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		int[] offset = CommonUtils.getAllOffsetValues();
		byte[] seven = new byte[0], eleven = new byte[0], twelve = new byte[0], thirteen = new byte[0];
		if(list.contains(2)){
			byte[] temp = Arrays.copyOfRange(source, 70, 72);
			offset[2] = Integer.parseInt(new String(temp)) + 2;
		}
		for(int i=2, from=70; i<=13; i++){
			if(list.contains(i)){
				if(i==7){
					seven = Arrays.copyOfRange(source, from, from + offset[i]);
				}
				if(i==11){
					eleven = Arrays.copyOfRange(source, from, from + offset[i]);
				}
				if(i==12){
					twelve = Arrays.copyOfRange(source, from, from + offset[i]);
				}
				if(i==13){
					thirteen = Arrays.copyOfRange(source, from, from + offset[i]);
				}
				from += offset[i];
			}
		}
		//读取目的报文信息，分析报文结构并计算出第7、11、12、13域相对于起始位置的偏移量	
		byte[] destination = FileUtils.readFileToByteArray(new File(DESTINATION_FILE_NAME));
		bitmap = Arrays.copyOfRange(destination, 54, 70);
		list = CommonUtils.toBinaryString(bitmap);
		int sevenOffset=0, elevenOffset=0, twelveOffset=0, thirteenOffset=0;
		if(list.contains(2)){
			byte[] temp = Arrays.copyOfRange(destination, 70, 72);
			offset[2] = Integer.parseInt(new String(temp)) + 2;
		}
		for(int i=2, count=70; i<=13; i++){
			count += offset[i];
			if(i==6){
				sevenOffset = count;
			}
			if(i==10){
				elevenOffset = count;
			}
			if(i==11){
				twelveOffset = count;
			}
			if(i==12){
				thirteenOffset = count;
			}
		}
		//将源报文的第7域信息替换目的报文第7域的信息
		for(int i=sevenOffset, j=0; j<seven.length; i++, j++){
			destination[i] = seven[j];
		}
		//将源报文的第11域信息替换目的报文第11域的信息
		for(int i=elevenOffset, j=0; j<eleven.length; i++, j++){
			destination[i] = eleven[j];
		}
		//将源报文的第12域信息替换目的报文第12域的信息
		for(int i=twelveOffset, j=0; j<twelve.length; i++, j++){
			destination[i] = twelve[j];
		}
		//将源报文的第13域信息替换目的报文第13域的信息
		for(int i=thirteenOffset, j=0; j<thirteen.length; i++, j++){
			destination[i] = thirteen[j];
		}
		//先提取出前壹個域的信息并拼装
		byte[] data = Arrays.copyOfRange(destination, 50, 54);
		data = mergeByte(data, new byte[]{32});
		data = mergeByte(data, bitmap);
		data = mergeByte(data, new byte[]{32});
		//重新分析目的报文，提取出前28個报文域的信息
		int from = 70;
		byte[] result = new byte[0];
		for(int i=2; i<=28; i++){
			if(list.contains(i)){
				byte[] msg = Arrays.copyOfRange(destination, from, from + offset[i]);
				msg = mergeByte(msg, new byte[]{32});
				data = mergeByte(data, msg);
				from += offset[i];
			}
		}
		//提取出第32、33、35域的报文域信息
		for(int i=32; i<=35; i++){
			if(list.contains(i)){
				byte[] temp = Arrays.copyOfRange(destination, from, from + 2);
				offset[i] = Integer.parseInt(new String(temp)) + 2;
				byte[] msg = Arrays.copyOfRange(destination, from, from + offset[i]);
				msg = mergeByte(msg, new byte[]{32});
				data = mergeByte(data, msg);
				from += offset[i];
			}
		}
		//提取出第36域的报文信息并拼装
		if(list.contains(36)){
			byte[] temp = Arrays.copyOfRange(destination, from, from + 3);
			offset[36] = Integer.parseInt(new String(temp)) + 3;
			byte[] msg = Arrays.copyOfRange(destination, from, from + offset[36]);
			msg = mergeByte(msg, new byte[]{32});
			data = mergeByte(data, msg);
			from += offset[36];
		}
		//提取出第37-42域的报文信息并拼装
		for(int i=37; i<=42; i++){
			if(list.contains(i)){
				byte[] msg = Arrays.copyOfRange(destination, from, from + offset[i]);
				msg = mergeByte(msg, new byte[]{32});
				data = mergeByte(data, msg);
				from += offset[i];
			}
		}
		data = Arrays.copyOfRange(data, 0, data.length - 1);
		//根据上述报文域信息计算新的MAC值
		byte[] key = JEncryption.hexStringToBytes("1111111111111111");
		byte[] vector = { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] mac = JEncryption.desAnsiMac(key, vector, data);
		//使用新MAC值替换目的报文最后八位
		int start = destination.length - 9, end = destination.length - 1;
		for(int i=start, j=0; i<end; i++, j++){
			destination[i] = mac[j];
		}
		//对比替换之前和之后的报文，将它们的差异打印到控制台
//		byte[] orignal = FileUtils.readFileToByteArray(new File(DESTINATION_FILE_NAME));
//		System.out.println("destination.length=" + destination.length);
//		System.out.println("orignal.length=" + orignal.length);
//		for(int i=0; i<orignal.length; i++){
//			if(orignal[i] != destination[i]){
//				System.out.println(i + " orignal=" + orignal[i] + " ====> destination=" + destination[i]);
//			}
//		}
	}
	
	static byte[] mergeByte(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length + 1];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
}