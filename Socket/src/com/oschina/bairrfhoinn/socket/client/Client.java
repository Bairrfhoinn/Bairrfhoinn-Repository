package com.oschina.bairrfhoinn.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
//�\�з�����
//�ڿ����_���M��Ŀ�D:\codebase\Socket\src��Ȼ��ݔ������javac com\oschina\bairrfhoinn\socket\client\Client.java
//java com\oschina\bairrfhoinn\socket\client\Client ���ӿ͑��˾Ϳ��Կ���Ч���ˡ�
public class Client {
     public static void main(String[] args) {
          try {
               Socket socket=new Socket("127.0.0.1", 30000);
               PrintStream ps= new PrintStream(socket.getOutputStream());
               ps.println( "���ã����ǿͻ���" );
               //��socket��Ӧ����������װ��BufferedReader
               BufferedReader br=new BufferedReader( new InputStreamReader(socket.getInputStream()));              
               //������ͨ��io����
               String line=br.readLine();
               System.out.println("���Է����������ݣ���"+line);
               br.close();
               ps.close();
               socket.close();
          } catch(IOException e) {
               e.printStackTrace();
          }
     }
}