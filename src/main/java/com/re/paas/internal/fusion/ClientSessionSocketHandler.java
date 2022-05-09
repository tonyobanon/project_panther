package com.re.paas.internal.fusion;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.reflect.TypeToken;
import com.re.paas.internal.classes.Json;

class ClientSessionSocketHandler {
	
	private static final String OP = "op";
	
	private static final String INIT = "init";
	private static final String SYNC = "sync";
	
	
	private static final String SESSION_ID = "sessionId";
	private static final String DATA = "data";
	
	private static final Map<String, Session> sessionsById = new HashMap<>();
	private static final Map<Integer, String> sessionIdsByHash = new HashMap<>();

    static void onMessage(Session session, String message) {
        
		Map<String, Object> m = Json.getGson().fromJson(message, new TypeToken<Map<String, Object>>() {
        }.getType());
		
		var op = (String) m.get(OP);
		var sessionId = (String) m.get(SESSION_ID);
		
		@SuppressWarnings("unchecked")
		var data = (Map<String, String>) m.get(DATA);
		
		switch (op) {
		case INIT: 
			init(sessionId, session);
			break;
		case SYNC: 
			DataNodeDelegateImpl.updateClientData(sessionId, data);
			break;
		}
    }
	
	private static void init(String sessionId, Session sess) {
		
		sessionsById.put(sessionId, sess);
		sessionIdsByHash.put(sess.hashCode(), sessionId);
	}
 
    static void onConnect(Session session) {
    }
 
    static void onClose(Session session, int status, String reason) {
    	
    	String sessionId = sessionIdsByHash.remove(session.hashCode());
        Session sess = sessionsById.remove(sessionId);
        
        assert sess == session;
        
        DataNodeDelegateImpl.clearClientData(sessionId);
    }
     
    static Session getSession(String id) {
    	return sessionsById.get(id);
    }
    
}
