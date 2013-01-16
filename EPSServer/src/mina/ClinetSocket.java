package mina;

public class ClinetSocket {
	private static ClinetSocket clinetSocket = new ClinetSocket();
	private UnsyncClientSupport client;

	private ClinetSocket() {
	}

	public static ClinetSocket getInstance()// 公共的静态的方法。
	{
		return clinetSocket;
	}

	public UnsyncClientSupport getClient() {
		return client;
	}

	public void setClient(UnsyncClientSupport client) {
		this.client = client;
	}
}