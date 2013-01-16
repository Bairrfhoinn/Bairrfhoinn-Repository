package com.oschina.bairrfhoinn.socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
//運行方法：
//在控制臺中進入目錄D:\codebase\Socket\src，然后輸入命令javac com\oschina\bairrfhoinn\socket\server\Server.java
//java com\oschina\bairrfhoinn\socket\server\Server 啟動客戶端就可以看到效果了。
public class Server {
     public static void main(String[] args) {
          //創建壹個ServerSocket，用于监听客户端Socket的连接请求
          try {
               ServerSocket socket = new ServerSocket(30000);
               //采用死循环不断接受来自客户端的请求
               while(true) {
                    //每当接受到客户端socket的请求，服务端也对应产生一个socket
                    Socket s = socket.accept();
                    System.out.println("IP为 "+s.getInetAddress()+" 连接此服务器");
                    BufferedReader br= new BufferedReader(new InputStreamReader(s.getInputStream()));
                    System.out.println( "来自客户端的数据：" +br.readLine());
                    //将Socket对应的输出流包装成PrintStream
                    PrintStream ps=new PrintStream(s.getOutputStream());
                    //进行普通的io操作
                    ps.println("客户端您好，我是服务器。");
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