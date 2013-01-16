package swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import mina.ClientSocket;
import mina.MinaLongConnServer;
import mina.UnsyncClientSupport;
import util.CUPConstant;

public class CUPSocketServer extends JFrame {

	private static final long serialVersionUID = 1L;
	JButton b1;
	JButton b2;
	JButton b3;
	JTextField tf;
	MinaLongConnServer server = null;
	static String fileName = "src/cuptocps";
	UnsyncClientSupport client;

	public CUPSocketServer() throws Exception {
		server = new MinaLongConnServer(CUPConstant.CUP_SERVER_PORT);
		server.start();
		this.setTitle("CUP");
		b1 = new JButton("Connect Client");
		this.add(b1);
		b2 = new JButton("Sent Message");
		this.add(b2);
		b3 = new JButton("Stop Sending");
		this.add(b3);
		this.setLayout(new FlowLayout());
		this.setBounds(200, 100, 300, 150);
		this.setVisible(true);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client = new UnsyncClientSupport(CUPConstant.CUP_SERVER_IP, 59005);
				try {
					client.connect();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				ClientSocket.getInstance().setClient(client);
				// b1.disable();
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				try {
//					client.send(FileUtil.readFile(fileName));
//				} catch (InterruptedException e2) {
//					e2.printStackTrace();
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//
//				 System.out.println(11);
//				 Initialization init = Initialization.getInstance();
//				 HashMap<String, IoSession> clientMap = init.getClientMap();
//				 if (clientMap.containsKey("127.0.0.1")) {
//					 IoSession longSession = clientMap.get("127.0.0.1");
//					 try {
//						 longSession.write(11);
//					 } catch (Exception e1) {
//						 e1.printStackTrace();
//					 }
//				 }
			}
		});
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(null != ClientSocket.getInstance().getClient()){
					ClientSocket.getInstance().getClient().stop();
				}
			}
		});
	}

	public static void main(String[] args) throws Exception {
		CUPSocketServer socketServer = new CUPSocketServer();
	}
}