package util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.mina.core.buffer.IoBuffer;

public class CommonUtils {
	static int serialNumber = 0;
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}
	
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static byte[] update(byte[] oldArray, boolean isHeaderExists) {
		byte[] array = Arrays.copyOf(oldArray, oldArray.length);
		boolean MESSAGE_HEADER_EXISTS = isHeaderExists;
		int MESSAGE_PREFIX = 4;
		int MESSAGE_HEADER_LENGTH = 46;
		int MESSAGE_TYPE_ID_LENGTH = 4;
		int BITMAP_LENGTH = 16;
		int MESSAGE_BODY_LENGTH = 128;
		int start = MESSAGE_PREFIX + MESSAGE_TYPE_ID_LENGTH;
		if(MESSAGE_HEADER_EXISTS){
			start += MESSAGE_HEADER_LENGTH;
		}
		byte[] bitmap = Arrays.copyOfRange(array, start, start + BITMAP_LENGTH);
		String a = CommonUtils.bytesToHexString(bitmap);		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i < BITMAP_LENGTH; i++){
			String byteStr = Integer.toBinaryString(CommonUtils.hexStringToBytes(a)[i] & 0xFF);
			while(byteStr.length() < 8){
				byteStr = "0" + byteStr;
			}
			sb.append(byteStr);
		}
		String binaryBitmap = sb.toString();
		int len = binaryBitmap.length();
		ArrayList list = new ArrayList();
		for(int i=1; i< len; i++){
			if(binaryBitmap.charAt(i) == '1'){
				list.add(i+1);
			}
		}
		int[] offsets = new int[MESSAGE_BODY_LENGTH];
		offsets = getAllOffsetValues();
		int startIndex = start + BITMAP_LENGTH;
		if(list.contains(2)){
			byte[] accountLengthBitMap = Arrays.copyOfRange(array, startIndex, startIndex + 2);
			int primaryAccountNumberLength = Integer.valueOf(new String(accountLengthBitMap));
			offsets[2] = primaryAccountNumberLength + 2;
		}		
		int region3 = startIndex, region7 = startIndex, region11 = startIndex;
		if(list.contains(2)){
			region3 += offsets[2];
		}
		for(int i=2; i<7; i++){
			if(list.contains(i)){
				region7 += offsets[i];
			}
		}
		for(int i=2; i<11; i++){
			if(list.contains(i)){
				region11 += offsets[i];
			}
		}
		int region12 = region11;
		if(list.contains(11)){
			region12 += offsets[11];
		}
		int region13 = region12;
		if(list.contains(12)){
			region13 += offsets[12];
		}
	 SimpleDateFormat df1 = new SimpleDateFormat("MMddHHmmss");
	 String currentDate = df1.format(new java.util.Date());
	 String systemTraceAuditNumber = generateSystemTraceAuditNumber();
	 array = replaceBytes(array, region7, currentDate);
	 array = replaceBytes(array, region12, currentDate.substring(4, 10));
	 array = replaceBytes(array, region13, currentDate.substring(0, 4));
	 array = replaceBytes(array, region11, systemTraceAuditNumber);
	 return array;
	}
	
	private static byte[] replaceBytes(byte[] array, int index, String str) {
		byte[] bytes = str.getBytes();
		int length = bytes.length;
		for(int i=index; i < length + index; i++){
			array[i] = bytes[i-index];
		}
		return array;
	}
	
	//生成壹個数组，每個下标依次对应壹個报文域的编号，每個元素的值是该报文域的长度
	public static int[] getAllOffsetValues() {
		int MESSAGE_BODY_LENGTH = 129;
		int[] result = new int[MESSAGE_BODY_LENGTH];
		for(int i=0; i<MESSAGE_BODY_LENGTH; i++){
			result[i] = 0;
		}
		result[2] = 21;
		result[3] = 6;
		result[4] = 12;
		result[5] = 12;
		result[6] = 12;
		result[7] = 10;
		result[9] = 8;
		result[10] = 8;
		result[11] = 6;
		result[12] = 6;
		result[13] = 4;
		result[14] = 4;
		result[15] = 4;
		result[16] = 4;
		result[18] = 4;
		result[19] = 3;
		result[22] = 3;
		result[23] = 3;
		result[25] = 2;
		result[26] = 2;
		result[28] = 9;
		result[32] = 13;
		result[33] = 13;
		result[35] = 39;
		result[36] = 107;
		result[37] = 12;
		result[38] = 6;
		result[39] = 2;
		result[41] = 8;
		result[42] = 15;
		result[43] = 40;
		result[49] = 3;
		result[50] = 3;
		result[51] = 3;
		result[52] = 8;
		result[53] = 16;
		result[54] = 43;
		result[55] = 258;
		result[57] = 103;
		result[58] = 103;
		result[59] = 603;
		result[60] = 103;
		result[61] = 203;
		result[62] = 203;
		result[63] = 203;
		result[70] = 3;
		result[90] = 42;
		result[96] = 8;
		result[100] = 13;
		result[102] = 30;
		result[100] = 13;
		result[102] = 30;
		result[103] = 30;
		result[121] = 103;
		result[122] = 103;
		result[123] = 103;
		result[128] = 8;
		return result;
	}
	
	public static void log(String str){
		if(true){
			System.out.println(str);
		}
	}
	
	private static String generateSystemTraceAuditNumber() {
		//生成六位数字字符串，从000000开始逐個递增
		if(serialNumber > 999999){
			serialNumber = 0;
		}
		Integer x = serialNumber ++;
		String result = x.toString();
		while(result.length() < 6){
			result = "0" + result;
		}
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList toBinaryString(byte[] bitmap) {
		String a = CommonUtils.bytesToHexString(bitmap);		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i < bitmap.length; i++){
			String byteStr = Integer.toBinaryString(CommonUtils.hexStringToBytes(a)[i] & 0xFF);
			while(byteStr.length() < 8){
				byteStr = "0" + byteStr;
			}
			sb.append(byteStr);
		}
		String binary = sb.toString();
		ArrayList result = new ArrayList();
		int lens = binary.length();
		for(int i=1; i< lens; i++){
			if(binary.charAt(i) == '1'){
				result.add(i+1);
			}
		}
		return result;
	}
	
	/*
	 * 获取报文的文件名更新后的格式：线路名称 + 第0域 + 第3域 + 第25域
	 * 如报文文件原名为Cps2CupAtmInquireReq，更新之后的文件名应为 Cps2Cup020030100002
	 * 其中0200是第零域，301000是第3域，02是第25域
	 */
	public static String getFileNameFromMsg(byte[] message){
		byte[] bitmap = Arrays.copyOfRange(message, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		int[] offset = CommonUtils.getAllOffsetValues();		
		int from = 50, to = 54;
		//报文的第零域
		byte[] zero = Arrays.copyOfRange(message, from, to);		
		if(list.contains(2)){
			//第二报文域存在，由于其长度是动态可变的，所以先计算该域总长度
			byte[] temp = Arrays.copyOfRange(message, 70, 72);
			offset[2] = Integer.valueOf(new String(temp)) + 2;
		}
		
		from = 70 + offset[2];
		to = from + offset[3];
		//报文的第三域
		byte[] three = Arrays.copyOfRange(message, from, to);
		//计算第二十五域相对于起始的偏移地址
		from = to;
		for(int i=4; i<25; i++){
			if(list.contains(i)){
				from += offset[i];
			}
		}
		to = from + offset[25];
		byte[] last = Arrays.copyOfRange(message, from, to);		
		byte[] result = new byte[zero.length + three.length + last.length];
		System.arraycopy(zero, 0, result, 0, zero.length);
		System.arraycopy(three, 0, result, zero.length, three.length);
		System.arraycopy(last, 0, result, zero.length + three.length, last.length);
		StringBuffer sb = new StringBuffer();
		for(byte i : result){
			sb.append(i-48);
		}
		return sb.toString();
	}
	
	public static String getString(Object message) {
		IoBuffer buffer = (IoBuffer) message;
		byte[] b = new byte[buffer.limit()];
		buffer.get(b);
		return new String(b);
	}
}