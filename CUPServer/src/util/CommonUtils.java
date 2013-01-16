package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
	
	//字节数组里存储的是某报文信息，分别按要求更新其中第7、11、12、13域的报文，完成后返回更新后的报文
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static byte[] update(byte[] array, boolean isHeaderExists) {
		//the length of bitmap, 16 byte
		int BITMAP_LENGTH = 16;
		//the amount of message block
		int MESSAGE_BODY_LENGTH = 128;
		//default offset of primary account number
		int start = 54;
		byte[] bitmap = Arrays.copyOfRange(array, 54, 70);
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
		log("The content of list is : " + list);
		int[] offsets = new int[MESSAGE_BODY_LENGTH];
		offsets = getAllOffsetValues();
		int startIndex = start + BITMAP_LENGTH;
		if(list.contains(2)){
			byte[] temp = Arrays.copyOfRange(array, startIndex, startIndex + 2);
			offsets[2] = Integer.valueOf(new String(temp)) + 2;
		}		
		int region7 = startIndex, region11 = startIndex;
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
	
	public static byte[] replaceBytes(byte[] array, int index, String str) {
		byte[] bytes = str.getBytes();
		int end = bytes.length + index;
		for(int i=index; i < end; i++){
			array[i] = bytes[i-index];
		}
		return array;
	}
	
	//获得壹個数组，每個下标依次对应壹個报文域的编号，每個元素的值是该报文域的默认长度
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

	/**
	 * 从报文域data中获取第i域的内容
	 * @param data 报文域的内容
	 * @param hasHeader 是否存在报文头
	 * @param i 指定第几域，取值范围在2-128之间
	 * @return
	 */
	public static byte[] getRegionFrom(byte[] data, int i, boolean hasHeader) {
		byte[] result;
		if(hasHeader){
			result = getRegionFromMessageWithHeader(data, i);
		}else{
			result = getRegionFromMessageWithoutHeader(data, i);
		}
		return result;
	}

	//从没有报文头的报文中取得第 i 域并返回
	@SuppressWarnings("rawtypes")
	private static byte[] getRegionFromMessageWithoutHeader(byte[] data, int i) {
		int from = 8, to = 24;
		int[] offset = getAllOffsetValues();
		byte[] bitmap = Arrays.copyOfRange(data, from, to);
		ArrayList list = getBinaryList(bitmap);
		if(!list.contains(i)){
			return new byte[0];
		}
		if(i < 2){
			return new byte[0];
		}
		if(i == 2){
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			int len = Integer.valueOf(new String(temp)) + 2;
			return Arrays.copyOfRange(data, to, to + len);
		}
		if(i > 2 && i < 32){
			//计算出第二域的偏移值
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			int len = Integer.valueOf(new String(temp)) + 2;
			to += len;
			//计算出第 i域的偏移值
			for(int j=3; j<i; j++){
				if(list.contains(j)){
					to += offset[j];
				}
			}
			return Arrays.copyOfRange(data, to, to + offset[i]);
		}
		return new byte[0];
	}

	//从包含报文头的报文中取得第 i 域并返回
	@SuppressWarnings("rawtypes")
	private static byte[] getRegionFromMessageWithHeader(byte[] data, int i) {
		int from = 54, to = 70;
		int[] offset = getAllOffsetValues();
		byte[] bitmap = Arrays.copyOfRange(data, from, to);
		ArrayList list = getBinaryList(bitmap);
		if(!list.contains(i)){
			return new byte[0];
		}
		if(i < 2){
			return new byte[0];
		}
		if(i == 2){
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			offset[i] = Integer.valueOf(new String(temp)) + 2;
			return Arrays.copyOfRange(data, to, to + offset[i]);
		}
		if(i > 2 && i < 32){
			//计算出第二域的偏移值
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			int len = Integer.valueOf(new String(temp)) + 2;
			to += len;
			//计算出第 i域的偏移值
			for(int j=3; j<i; j++){
				if(list.contains(j)){
					to += offset[j];
				}
			}
			return Arrays.copyOfRange(data, to, to + offset[i]);
		}
		return new byte[0];
	}
	
	//将二进制数组转化成0和1组成的字符串集合并返回
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static ArrayList getBinaryList(byte[] bitmap) {
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
		int len = binary.length();
		for(int i=1; i< len; i++){
			if(binary.charAt(i) == '1'){
				result.add(i+1);
			}
		}
		return result;
	}

	/**
	 * 使用报文域 block 替换报文 data 中第 i 域的内容
	 * @param data 报文的内容
	 * @param block 报文域的内容
	 * @param i 报文域的编号
	 * @param hasHeader 报文是否含有报文头
	 */
	public static void replaceData(byte[] data, byte[] block, int i, boolean hasHeader) {
		int from = 8, to = 24;
		int[] offset = getAllOffsetValues();
		if(hasHeader){
			from += 46;
			to += 46;
		}
		if(i == 2){
			//替换第二域
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			//取得第二域的总长度
			int len = Integer.valueOf(new String(temp)) + 2;
			//循环替换每個字节
			for(int j = to; j < to + len; j++){
				data[j] = block[j-to];
			}
		}
		if(i > 2 && i < 32){
			byte[] temp = Arrays.copyOfRange(data, to, to + 2);
			int len = Integer.valueOf(new String(temp)) + 2;
			from = to;
			to += len;
			//循环遍历各域的长度并计算总的偏移量
			for(int j=3; j<=i; j++){
				len = offset[j];
				from = to;
				to += offset[j];
			}
			for(int j=from, k=to; j<k; j++){
				data[j] = block[j-from];
			}
		}
	}

	//合并两個byte[]数组，中间用壹個空格符分隔
	public static byte[] combineByte(byte[] byte_1, byte[] byte_2){
		byte[] result = new byte[byte_1.length + byte_2.length + 1];
		result = byteMerger(byte_1, new byte[]{32});
		result = byteMerger(result, byte_2);
		return result;
	}

	//合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
		byte[] byte_3 = new byte[byte_1.length+byte_2.length+1];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
	
	//从字节数组中获取指定的所有报文域并封装成指定结构并返回
	@SuppressWarnings("rawtypes")
	public static byte[] collectDataForMacEncryption(byte[] data) {
		//报文类型 + 位图 + 主帐号 + 交易处理码 + 交易传输时间 + 系统跟踪号 + 受卡方所在地时间
		byte[] result = Arrays.copyOfRange(data, 50, 54);
		byte[] tem = Arrays.copyOfRange(data, 54, 70);
		byte[] bitmap = Arrays.copyOf(tem, tem.length);
		result = combineByte(result, tem);
		int[] offset = getAllOffsetValues();		
		ArrayList results = toBinaryString(bitmap);
		int len = 0;
		if(results.contains(2)){
			//第二域存在于位图中位，计算第二域占据的总长度
			tem = Arrays.copyOfRange(data, 70, 72);
			len = Integer.parseInt(new String(tem)) + 2;
			tem = Arrays.copyOfRange(data, 70, len + 70);
			result = combineByte(result, tem);
		}
		//两個坐标，记录每個域的起点和终点偏移量，当前记录的是第三個域的起点和终点偏移量
		int from = len + 70, to = from + offset[3];
		for(int i=3; i<32; i++){
			if(results.contains(i)){
				tem = Arrays.copyOfRange(data, from, to);
				result = combineByte(result, tem);
				from = to; 
				to += offset[i+1];
			}else{
				to = from + offset[i+1];
			}
		}
		for(int i=32; i<37; i++){
			if(results.contains(i)){
				//此时from, to已经记录了下壹個域的起点和终点偏移量
				tem = Arrays.copyOfRange(data, from, to);
				result = combineByte(result, tem);
				from = to;
				to += offset[i+1];
			}else{
				to = from + offset[i+1];
			}
		}
		for(int i=37; i<=42; i++){
			if(results.contains(i)){
				tem = Arrays.copyOfRange(data, from, to);
				result = combineByte(result, tem);
				from = to; 
				to += offset[i+1];
			}else{
				to = from + offset[i+1];
			}
		}
		return result;
	}

	//将字节数组的每壹位分别转化成0和1，并记录所有1在数组中出现的位置，起始位为第1位，最后将位置集合返回
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	@SuppressWarnings("rawtypes")
	public static String getFileNameFromMsg(byte[] message) {
		byte[] bitmap = Arrays.copyOfRange(message, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		int[] offset = CommonUtils.getAllOffsetValues();		
		int from = 50, to = 54;
		//报文的第零域
		byte[] zero = Arrays.copyOfRange(message, from, to);		
		if(list.contains(2)){
			//第二域在报文中存在，则读取该域真正的长度并存储于数组offsets中
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
	
	//将字节数组转化为由二进制数字组成的字符串并返回
	public static String byteToBinaryString(byte[] data){
		StringBuilder sb1 = new StringBuilder("");
		StringBuffer sb = new StringBuffer("");
		if (data == null || data.length <= 0) {
			return null;
		}
		for (int i = 0; i < data.length; i++) {
			int v = data[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				sb1.append(0);
			}
			sb1.append(hv);
		}
		byte[] a = CommonUtils.hexStringToBytes(sb1.toString());		
		for(int i=0; i < data.length; i++){
			String temp = Integer.toBinaryString(a[i] & 0xFF);
			while(temp.length() < 8){
				temp = "0" + temp;
			}
			sb.append(temp);
		}
		return sb.toString();
	}

	//取得第32域报文中的四位标识码，如32域的的报文内容是0803011200，则返回结果应为0301
	@SuppressWarnings("rawtypes")
	public static String getAiic(byte[] original) {
		byte[] bitmap = Arrays.copyOfRange(original, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		int[] offset = CommonUtils.getAllOffsetValues();
		int from = 70;
		if(list.contains(2)){
			byte[] temp = Arrays.copyOfRange(original, from, from + 2);
			offset[2] = Integer.parseInt(new String(temp)) + 2;
		}
		for(int i=2; i<=28; i++){
			if(list.contains(i)){
				from += offset[i];
			}
		}
		if(list.contains(32)){
			byte[] temp = Arrays.copyOfRange(original, from, from + 2);
			offset[32] = Integer.parseInt(new String(temp)) + 2;
			byte[] result = Arrays.copyOfRange(original, from + 2, from + 6);
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<result.length; i++){
				sb.append(result[i] - 48);
			}
			return sb.toString();
		}
		return "";
	}
	
	public static String readFile(String fileName) throws Exception {
		String str = "";// 每行的内容
		InputStreamReader reader = new InputStreamReader(new FileInputStream(
				fileName), "ASCII");
		BufferedReader br = new BufferedReader(reader);
		while ((str = br.readLine()) != null) {
			return str;
		}
		return "";
	}
	
	public static String getString(Object message) {
		IoBuffer buffer = (IoBuffer) message;
		byte[] b = new byte[buffer.limit()];
		buffer.get(b);
		return new String(b);
	}
}