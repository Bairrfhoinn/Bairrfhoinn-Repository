package swing;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import util.EPSConstant;

import mina.ClinetSocket;
import mina.MinaLongConnServer;
import mina.UnsyncClientSupport;

public class EPSocketServer extends JFrame {

	private static final long serialVersionUID = 1L;
	JButton b1;
	JTextField tf;
	MinaLongConnServer server = null;
	static String fileName = "src/Eps2CpsAtmInquireReq";
	JButton b2;
	JButton b3;
	UnsyncClientSupport client;

	public EPSocketServer() throws Exception {
		server = new MinaLongConnServer(EPSConstant.EPS_SERVER_PORT);
		server.start();
		b1 = new JButton("Connect Client");
		this.add(b1);
		this.setTitle("EPS");
		b2 = new JButton("Start Sending");
		this.add(b2);
		b3 = new JButton("Stop Sending");
		this.add(b3);
		this.setLayout(new FlowLayout());
		this.setBounds(200, 100, 300, 150);
		this.setVisible(true);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client = new UnsyncClientSupport(EPSConstant.EPS_CLIENT_IP, EPSConstant.EPS_CLIENT_PORT);
				try {
					client.connect();
					ClinetSocket.getInstance().setClient(client);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					client.send(readFile(fileName));
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//停止发送报文
				client.stop();
			}
		});
	}

	public static String readFile(String fileName) throws Exception {
		String str = "";// 每行的内容

		InputStreamReader reader = new InputStreamReader(new FileInputStream(
				fileName), "ASCII");
		BufferedReader br = new BufferedReader(reader);
		while ((str = br.readLine()) != null) {
			br.close();
			return str;
		}
		br.close();
		return "";
	}

	public static void main(String[] args) throws Exception {
		EPSocketServer st = new EPSocketServer();
	}
}