package example;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import util.CommonUtils;

public class ModifyFileNameExample {
	
	public static void main(String[] args) throws IOException {
		//测试方法CommonUtils.getFileNameFromMsg()...
		byte[] message = FileUtils.readFileToByteArray(new File("src/Cup2CpsAtmInqureRsp"));
		System.out.println(CommonUtils.getFileNameFromMsg(message));//expected result: 021030100002
		message = FileUtils.readFileToByteArray(new File("src/Cup2CpsAuthCancelRsp"));
		System.out.println(CommonUtils.getFileNameFromMsg(message));//expected result: 011020000006
		message = FileUtils.readFileToByteArray(new File("src/Cup2CpsAuthRsp"));      
		System.out.println(CommonUtils.getFileNameFromMsg(message));//expected result: 011003000006
		message = FileUtils.readFileToByteArray(new File("src/Cup2CpsPosInquireRsp"));
		System.out.println(CommonUtils.getFileNameFromMsg(message));//expected result: 021030000000
		message = FileUtils.readFileToByteArray(new File("src/Cup2CpsWithdrawRsp"));  
		System.out.println(CommonUtils.getFileNameFromMsg(message));//expected result: 021001100002
	}
}