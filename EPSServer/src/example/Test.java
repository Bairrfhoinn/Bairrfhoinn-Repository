package example;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Test {
	static String fileName = "src/1801msg";

	public static void main(String[] args) throws Exception {
		System.out
				.println("6039318A51601                .036900010000   03010000   000000000000000000"
						.length());
		// System.out.println(readFile());
	}

	public static String readFile() throws Exception {
		String str = "";// 每行的内容
		InputStreamReader reader = new InputStreamReader(new FileInputStream(
				fileName), "gbk");
		BufferedReader br = new BufferedReader(reader);
		while ((str = br.readLine()) != null) {
			return str;
		}
		return "";
	}

}
