package mina;

public class ClientSocket {
	private static ClientSocket clientSocket = new ClientSocket();
	private UnsyncClientSupport client;

	private ClientSocket() {
	}

	public static ClientSocket getInstance()
	{
		return clientSocket;
	}

	public UnsyncClientSupport getClient() {
		return client;
	}

	public void setClient(UnsyncClientSupport client) {
		this.client = client;
	}
}