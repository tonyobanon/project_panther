package com.re.paas.internal.fusion;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class ClientSessionSocketAdapter extends WebSocketAdapter {

	private Session session;
	
	@Override
	public void onWebSocketConnect(Session sess) {
		ClientSessionSocketHandler.onConnect(sess);
		this.session = sess;
	}
	
	@Override
	public void onWebSocketClose(int statusCode, String reason) {
		ClientSessionSocketHandler.onClose(this.session, statusCode, reason);
		this.session = null;
	}
	
	@Override
	public void onWebSocketText(String message) {
		ClientSessionSocketHandler.onMessage(this.session, message);
	}
	
}
