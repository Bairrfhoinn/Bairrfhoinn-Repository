package com.mingluck.example;

import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class WriteXmlFileExample {

	public static void main(String[] args) throws DocumentException {
		Document doc = DocumentHelper.createDocument();
		Element ebank = doc.addElement("ebank");
		Element gDecl = ebank.addElement("gDecl");
		gDecl.setText("");
		Element data = ebank.addElement("gTxData");
		Element cHostCod = data.addElement("cHostCod");
		cHostCod.setText("820001");
		Element cRspCod = data.addElement("cRspCod");
		cRspCod.setText("SC0000");
		Element rescode = data.addElement("rescode");
		rescode.setText("ISS0000");
		Element resmsg = data.addElement("resmsg");
		resmsg.setText("成功");
		
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileOutputStream("src/output.xml"), format);
			writer.write(doc);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("已成功创建XML文件...");
	}
}