package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class BytesUtils {
	@SuppressWarnings("rawtypes")
	public static byte[] updateMacAndBlocks(byte[] source, byte[] destination) throws IOException{
		//长度说明 source==>472  destination==>366
		byte[] bitmap = Arrays.copyOfRange(source, 54, 70);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		System.out.println("存在的报文域分别是：" + list);
		int[] offsets = CommonUtils.getAllOffsetValues();
		if(list.contains(2)){
			//第二域在报文中存在，则读取该域真正的长度并存储于数组offsets中
			byte[] x = Arrays.copyOfRange(source, 70, 72);
			offsets[2] = Integer.valueOf(new String(x)) + 2;
			System.out.println("第二域长度为：" + offsets[2] + " 起始长度为：70");
		}
		//读取第七域的字节并分别存储于变量中
		int from = 70;
		for(int i=2; i <7; i++){
			if(list.contains(i)){
				from += offsets[i];
			}
		}
		int to = from + offsets[7];
		System.out.println("第七域长度为：" + offsets[7] +" 起始长度为：" + from);
		byte[] seven = Arrays.copyOfRange(source, from, to);
		for(int i=0; i<seven.length; i++){
			System.out.print(seven[i] + " ");
		}
		System.out.println();
		
		//如果第11域壹定存在
		from = to;
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

		int len = destination.length;
		for(int i=len-9, j=0; i<len-1;i++, j++){
			destination[i] = mac[j];
		}
		return destination;
	}

	@SuppressWarnings("rawtypes")
	public static byte[] getData(byte[] result) {
		byte[] bitmap = Arrays.copyOfRange(result, 54, 70);
		//para是计算Mac值的参数之壹
		byte[] para = CommonUtils.combineByte(Arrays.copyOfRange(result, 50, 54), bitmap);
		ArrayList list = CommonUtils.toBinaryString(bitmap);
		//offsets中每個报文域的默认长度
		int[] offsets = CommonUtils.getAllOffsetValues();
		//报文域的起始位置在整個报文中的位置偏移量
		int start = 70, end = 72;
		if(list.contains(2)){
			byte[] tem = Arrays.copyOfRange(result, start, end);
			offsets[2] = Integer.valueOf(new String(tem)) + 2;
		}
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, 70, 70 + offsets[2]));
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, 70 + offsets[2], 76 + offsets[2]));
		int count = 0;
		for(int i=4; i<7; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		//在参数中添加第7域的内容
		start = 76 + offsets[2] + count;
		end = start + offsets[7];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		if(list.contains(9)){
			count += offsets[9];
		}
		if(list.contains(10)){
			count += offsets[10];
		}
		start = end + count;
		end = start + offsets[11];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		for(int i=12; i<18; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		start = end + count;
		end = start + offsets[18];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		for(int i=19; i<25; i++){
			if(list.contains(i)){
				count += offsets[i];
			}
		}
		start = end + count;
		end = start + offsets[25];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
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
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		count = 0;
		if(list.contains(33)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[33] = Integer.valueOf(new String(tem)) + 2;
		}
		start = end;
		end = start + offsets[33];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		if(list.contains(35)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[35] = Integer.valueOf(new String(tem)) + 2;
		}
		start = end; 
		end = start + offsets[35];
		para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		if(list.contains(36)){
			byte[] tem = Arrays.copyOfRange(result, end, end + 2);
			offsets[36] = Integer.valueOf(new String(tem)) + 2;
		}
		for(int i=36; i<=37; i++){
			start = end;
			end = start + offsets[i];
			para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		}
		count = 0;
		if(list.contains(38)){
			count += offsets[38];
		}
		end += count;
		for(int i=39; i<=42; i++){
			start = end;
			end = start + offsets[i];
			para = CommonUtils.combineByte(para, Arrays.copyOfRange(result, start, end));
		}
		return para;
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
	
	
	public static byte[] writeByte(byte[] messages, ProtocolDecoderOutput out,
			MinaByteBean bean) {
		try {
			int totalNumber = bean.getTotalNumber();
			byte[] numberBytes = null;
			byte[] returnMessage = null;
			if (messages.length < 4) {
				return messages;
			} else if (totalNumber == 0) {
				numberBytes = Arrays.copyOf(messages, 4);
				if ("0000".equalsIgnoreCase(new String(numberBytes))) {
					out.write(IoBuffer.wrap(numberBytes));
					bean.setTotalNumber(0);
					returnMessage = writeByte(ArrayUtils.subarray(messages, 4,
							messages.length), out, bean);
				} else {
					totalNumber = Integer.valueOf(new String(numberBytes));
					bean.setTotalNumber(totalNumber);
					returnMessage = writeByte(messages, out, bean);
				}
			} else if (totalNumber > 0) {
				if (messages.length > totalNumber + 4) {
					out.write(IoBuffer.wrap(ArrayUtils.subarray(messages, 0,
							totalNumber + 4)));
					bean.setTotalNumber(0);
					returnMessage = writeByte(ArrayUtils.subarray(messages,
							totalNumber + 4, messages.length), out, bean);
				} else if (messages.length == totalNumber + 4) {
					out.write(IoBuffer.wrap(messages));
					bean.setTotalNumber(0);
				} else {
					returnMessage = messages;
				}
			}
			return returnMessage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	public static void main(String[] args) throws IOException{
		byte[] input = FileUtils.readFileToByteArray(new File("src/message/Cps2CupAtmInquireReq"));
		byte[] output = FileUtils.readFileToByteArray(new File("src/message/Cup2CpsAtmInqureRsp"));
		byte[] compare = FileUtils.readFileToByteArray(new File("src/message/Cup2CpsAtmInqureRsp"));
		byte[] result = BytesUtils.updateMacAndBlocks(input, output);
		int olength = compare.length, rlength = result.length;
		System.out.println("olength=" + olength + " rlength=" + rlength);
		for(int i=0; i<olength; i++){
			if(!Integer.toHexString(compare[i] & 0xFF).equals(Integer.toHexString(result[i] & 0xFF))){
				System.out.println("i=" + i + " compare[i]=" + compare[i] +" result[i]=" + result[i]);
			}
		}
		System.out.println("complete the program...");
	}
}
