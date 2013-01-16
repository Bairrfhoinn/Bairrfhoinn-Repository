package com.mingluck.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class SmServerHandler extends IoHandlerAdapter {
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		IoBuffer buffer = (IoBuffer) message;
		byte[] in = new byte[buffer.limit()];
		buffer.get(in);
		System.out.println("���յ����ַ������鳤��Ϊ��" + in.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(in);		
		SAXReader reader =  new SAXReader();
		Document document = reader.read(bais);
		bais.close();
		Element root = document.getRootElement();
		Element gTxData = (Element)root.elements().get(1);
		String cHostCondition = gTxData.element("cHostCod").getText();
		//�����µ�XML�ĵ�
		Document doc = DocumentHelper.createDocument();
		doc.setXMLEncoding("GB2312");
		Element ebank = doc.addElement("ebank");
		Element gDecl = ebank.addElement("gDecl");
		gDecl.setText("");
		Element data = ebank.addElement("gTxData");
		Element cHostCod = data.addElement("cHostCod");
		cHostCod.setText(cHostCondition);
		Element cRspCod = data.addElement("cRspCod");
		cRspCod.setText("SC0000");
		Element rescode = data.addElement("rescode");
		rescode.setText("ISS0000");
		Element resmsg = data.addElement("resmsg");
		resmsg.setText("�ɹ�");
		try {
			//�����ɵ�XML�ĵ�����������ļ���
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("GB2312");
			XMLWriter writer = new XMLWriter(new FileOutputStream("src/output.xml"), format);
			writer.write(doc);
			writer.close();
			//�����ɵ�XML�ĵ�����д���ֽ������
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(output);
			out.writeObject(doc);
			out.close();
			//���ֽ������д��SOCKET��
			session.write(IoBuffer.wrap(output.toByteArray()));
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		cause.printStackTrace();
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		super.sessionIdle(session, status);
	}
}