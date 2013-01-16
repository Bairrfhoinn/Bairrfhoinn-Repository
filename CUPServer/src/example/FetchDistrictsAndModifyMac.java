package example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

import util.CommonUtils;
import util.JEncryption;

public class FetchDistrictsAndModifyMac {
	// 取出字节数组source中第7、11、12、13域的结果并将其放入字节数组destination中对应的域中，然后更新destination最后八位对应的MAC码
	public static void main(String[] args) throws IOException {
		//从source中获取需要的字节
		byte[] source = FileUtils.readFileToByteArray(new File("src/message/Cps2CupAtmInquireReq"));
		//替换destination中对应的字节
		byte[] destination = FileUtils.readFileToByteArray(new File("src/message/Cup2CpsAtmInqureRsp"));
		//destination更新前后的参照物，用于查看更新前后的变化内容
		byte[] destinationReference = FileUtils.readFileToByteArray(new File("src/message/Cup2CpsAtmInqureRsp"));
		//更新字节数组destination并返回更新后的结果
		destination = updateMacAndBlocks(source, destination);
		//以下内容用于对比更新前后的内容变化是否正确
		System.out.println("After updated the array destination, the difference between new and old:");
		for(int i=70; i < destinationReference.length; i++){
			if(!Integer.toHexString(destinationReference[i] & 0xFF).equals(Integer.toHexString(destination[i] & 0xFF))){
				System.out.println(i + ":[" + destination[i] + "]:[" + destinationReference[i] + "]");
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static byte[] updateMacAndBlocks(byte[] source, byte[] destination) throws IOException{
		byte[] bitmap = Arrays.copyOfRange(source, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		int[] offsets = CommonUtils.getAllOffsetValues();
		int from = 70;

		if(list.contains(2)){
			//第二域在报文中存在，则读取该域真正的长度并存储于数组offsets中
			byte[] x = Arrays.copyOfRange(source, from, from + 2);
			offsets[2] = Integer.valueOf(new String(x)) + 2;
		}
		//读取第七域的字节并分别存储于变量中
		for(int i=2; i <7; i++){
			if(list.contains(i)){
				from += offsets[i];
			}
		}
		//如果第七域壹定存在
		int to = from + offsets[7];//from = 97 to = 107
		byte[] seven = Arrays.copyOfRange(source, from, to);
		//seven = [49, 50, 49, 54, 48, 48, 52, 48, 49, 48]
		from = to;//from=107
		for(int i=9; i<11; i++){
			if(list.contains(i)){
				from += offsets[i];
			}
		}
		//如果第11域壹定存在
		to = from + offsets[11];//to=113
		byte[] eleven = Arrays.copyOfRange(source, from, to);
		//eleven = [48, 51, 52, 57, 49, 49]
		from = to;//from=113 to=119
		to = from + offsets[12];
		byte[] twelve = Arrays.copyOfRange(source, from, to);
		//twelve = [48, 48, 52, 48, 49, 48]
		from = to;//from = 119
		to = from + offsets[13]; //to = 123
		byte[] thirteen = Arrays.copyOfRange(source, from, to);	
		//thirteen = [49, 50, 49, 54]
		//第7、11、12、13域均截取完毕，接下来获取目的报文中第7、11、12、13域相对于起始的偏移量
		bitmap = Arrays.copyOfRange(destination, 54, 70);
		list = CommonUtils.toBinaryString(bitmap);
		if(list.contains(2)){
			//第二域在报文中存在，则读取该域真正的长度并存储于数组offsets中
			byte[] x = Arrays.copyOfRange(source, 70, 72);
			offsets[2] = Integer.valueOf(new String(x)) + 2;			
		}
		//开始计算第七域相对于起始的偏移量
		from = 70;
		for(int i=2; i<7; i++){
			if(list.contains(i)){
				from += offsets[i];
			}
		}
		//将字节数组seven中的内容替换当前destination中的相应值
		for(int i=from, j=0; j < seven.length; i++, j++){
			destination[i] = seven[j];
		}
		from += seven.length;
		//开始计算第11域相对于起始的偏移量
		for(int i=9; i<11; i++){
			if(list.contains(i)){
				from += offsets[i];
			}
		}
		//将字节数组eleven中的内容替换当前destination中的相应值
		for(int i=from, j=0; j < eleven.length; i++, j++){
			destination[i] = eleven[j];
		}
		from += eleven.length;
		//将字节数组twelve中的内容替换当前destination中的相应值
		for(int i=from, j=0; j < twelve.length; i++, j++){
			destination[i] = twelve[j];
		}
		from += twelve.length;
		//将字节数组thirteen中的内容替换当前destination中的相应值
		for(int i=from, j=0; j<thirteen.length; i++, j++){
			destination[i] = thirteen[j];
		}
		byte[] key = JEncryption.hexStringToBytes("1111111111111111");
		byte[] vector = { 0, 0, 0, 0, 0, 0, 0, 0 };
		byte[] data = getData(destination);
		byte[] mac = JEncryption.desAnsiMac(key, vector, data);
		
		return updateMac(destination, mac);
	}

	@SuppressWarnings("rawtypes")
	public static byte[] getData(byte[] result) {
		byte[] bitmap = Arrays.copyOfRange(result, 54, 70);
		//para是计算Mac值的参数之壹
		byte[] para = byteMerger(Arrays.copyOfRange(result, 50, 54), new byte[]{20});
		para = byteMerger(para, bitmap);
		//二进制形式保存的位图信息
		String binaryBitmap = convertToString(bitmap);
		//list中保存所有存在的报文域编号
		ArrayList list = getAllExistRegionsFrom(binaryBitmap);
		//offsets中每個报文域的默认长度
		int[] offsets = CommonUtils.getAllOffsetValues();
		//报文域的起始位置在整個报文中的位置偏移量
		int start = 70, end = 72;
		if(list.contains(2)){
			byte[] tem = Arrays.copyOfRange(result, start, end);
			offsets[2] = Integer.valueOf(new String(tem)) + 2;
		}
		para = combineByte(para, Arrays.copyOfRange(result, 70, 70 + offsets[2]));
		para = combineByte(para, Arrays.copyOfRange(result, 70 + offsets[2], 76 + offsets[2]));
		int count = 0;
		for(int i=4; i<7; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		//在参数中添加第7域的内容
		start = 76 + offsets[2] + count;
		end = start + offsets[7];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		if(list.contains(9)){
			count += offsets[9];
		}
		if(list.contains(10)){
			count += offsets[10];
		}
		start = end + count;
		end = start + offsets[11];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		for(int i=12; i<18; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		start = end + count;
		end = start + offsets[18];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		for(int i=19; i<25; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		start = end + count;
		end = start + offsets[25];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		if(list.contains(26)){
			count += offsets[26];
		}
		if(list.contains(28)){
			count += offsets[28];
		}
		start = end + count;
		if(list.contains(32)){
			byte[] tem = Arrays.copyOfRange(result, start, start + 2);
			offsets[32] = Integer.valueOf(new String(tem)) + 2;
		}
		end = start + offsets[32];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		if(list.contains(33)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[33] = Integer.valueOf(new String(tem)) + 2;
		}
		start = end;
		end = start + offsets[33];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		if(list.contains(35)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[35] = Integer.valueOf(new String(tem)) + 2;
		}
		start = end; 
		end = start + offsets[35];
		para = combineByte(para, Arrays.copyOfRange(result, start, end));
		if(list.contains(36)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[36] = Integer.valueOf(new String(tem)) + 2;
		}
		for(int i=36; i<=37; i++){
			start = end;
			end = start + offsets[i];
			para = combineByte(para, Arrays.copyOfRange(result, start, end));
		}
		count = 0;
		if(list.contains(38)){
			count += offsets[38];
		}
		end += count;
		for(int i=39; i<=42; i++){
			start = end;
			end = start + offsets[i];
			para = combineByte(para, Arrays.copyOfRange(result, start, end));
		}
		return para;
	}
	
	public static byte[] updateMac(byte[] result, byte[] mac) {
		int len = result.length;
		for(int i=len-9, j=0; i<len-1;i++, j++){
			result[i] = mac[j];
		}
		return result;
	}
	
	//合并两个byte数组
	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
		byte[] byte_3 = new byte[byte_1.length+byte_2.length+1];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
	
	public static String convertToString(byte[] bytes) {
		String a = CommonUtils.bytesToHexString(bytes);		
		StringBuffer sb = new StringBuffer();
		for(int i=0; i < bytes.length; i++){
			String byteStr = Integer.toBinaryString(CommonUtils.hexStringToBytes(a)[i] & 0xFF);
			while(byteStr.length() < 8){
				byteStr = "0" + byteStr;
			}
			sb.append(byteStr);
		}
		return sb.toString();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList getAllExistRegionsFrom(String binary) {
		ArrayList result = new ArrayList();
		int len = binary.length();
		for(int i=1; i< len; i++){
			if(binary.charAt(i) == '1'){
				result.add(i+1);
			}
		}
		System.out.println("The content of list is : " + result);
		return result;
	}
	
	//合并两個byte[]数组，中间用壹個空格符分隔
	public static byte[] combineByte(byte[] byte_1, byte[] byte_2){
		byte[] result = new byte[byte_1.length + byte_2.length + 1];
		result = byteMerger(byte_1, new byte[]{32});
		result = byteMerger(result, byte_2);
		return result;
	}
}