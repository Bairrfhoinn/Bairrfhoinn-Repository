package mina;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;

public class Initialization {
	private static Initialization instance;
	private HashMap<String, IoSession> clientMap;

	public Initialization() {

	}

	public static Initialization getInstance() {
		if (instance == null)
			instance = new Initialization();

		return instance;
	}

	public void init() {
		if (clientMap == null)
			clientMap = new HashMap<String, IoSession>();
	}

	public HashMap<String, IoSession> getClientMap() {
		return clientMap;
	}

	public void setClientMap(HashMap<String, IoSession> clientMap) {
		this.clientMap = clientMap;
	}

	public void removeClientMap(String mapKey) {
		if (StringUtils.isNotBlank(mapKey) && clientMap != null
				&& clientMap.containsKey(mapKey)) {
			clientMap.remove(mapKey);
		}
	}
}