package com.mingluck.example;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ReadXmlFileExample {
	public static void main(String[] arguments) throws DocumentException{
		SAXReader reader =  new SAXReader();
		Document document = reader.read(new File("src/input.xml"));
		Element root = document.getRootElement();
		Element gTxData = (Element)root.elements().get(1);
		String cHostCondition = gTxData.element("cHostCod").getText();
		System.out.println("cHostCondition=" + cHostCondition);
		String serverId = gTxData.element("srvid").getText();
		System.out.println("serverId=" + serverId);
		String branchNo = gTxData.element("branchno").getText();
		System.out.println("branchNo=" + branchNo);
		String immedFlag = gTxData.element("immedflag").getText();
		System.out.println("immedFlag=" + immedFlag);
		String lastSndTime = gTxData.element("lastsndtime").getText();
		System.out.println("lastSndTime=" + lastSndTime);
		String serverAccountNumber = gTxData.element("srvaccno").getText();
		System.out.println("serverAccountNumber=" + serverAccountNumber);
		String objAddress = gTxData.element("objaddr").getText();
		System.out.println("objAddress=" + objAddress);
		String messageCondition = gTxData.element("msgcont").getText();
		System.out.println("messageCondition=" + messageCondition);
	}
}
