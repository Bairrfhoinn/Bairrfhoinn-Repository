package com.oschina.bairrfhoinn.socket.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
//運行方法：
//在控制臺中進入目錄D:\codebase\Socket\src，然后輸入命令javac com\oschina\bairrfhoinn\socket\client\Client.java
//java com\oschina\bairrfhoinn\socket\client\Client 啟動客戶端就可以看到效果了。
public class Client {
     public static void main(String[] args) {
          try {
               Socket socket=new Socket("127.0.0.1", 30000);
               PrintStream ps= new PrintStream(socket.getOutputStream());
               ps.println( "您好，我是客户端" );
               //将socket对应的输入流包装成BufferedReader
               BufferedReader br=new BufferedReader( new InputStreamReader(socket.getInputStream()));              
               //进行普通的io操作
               String line=br.readLine();
               System.out.println("来自服务器的数据：："+line);
               br.close();
               ps.close();
               socket.close();
          } catch(IOException e) {
               e.printStackTrace();
          }
     }
}