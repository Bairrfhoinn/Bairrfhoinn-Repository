package com.oschina.bairrfhoinn.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
//�\�з�����
//�ڿ����_���M��Ŀ�D:\codebase\Socket\src��Ȼ��ݔ������javac com\oschina\bairrfhoinn\socket\server\Server.java
//java com\oschina\bairrfhoinn\socket\server\Server ���ӿ͑��˾Ϳ��Կ���Ч���ˡ�
public class Server {
     public static void main(String[] args) {
          //����Ҽ��ServerSocket�����ڼ����ͻ���Socket����������
          try {
               ServerSocket socket = new ServerSocket(30000);
               //������ѭ�����Ͻ������Կͻ��˵�����
               while(true) {
                    //ÿ�����ܵ��ͻ���socket�����󣬷����Ҳ��Ӧ����һ��socket
                    Socket s = socket.accept();
                    System.out.println("IPΪ "+s.getInetAddress()+" ���Ӵ˷�����");
                    BufferedReader br= new BufferedReader(new InputStreamReader(s.getInputStream()));
                    System.out.println( "���Կͻ��˵����ݣ�" +br.readLine());
                    //��Socket��Ӧ���������װ��PrintStream
                    PrintStream ps=new PrintStream(s.getOutputStream());
                    //������ͨ��io����
                    ps.println("�ͻ������ã����Ƿ�������");
                    ps.close();
                    br.close();
                    s.close();
                    socket.close();
               }
          } catch (IOException e) {
               e.printStackTrace();
          }
     }
}