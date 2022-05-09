package com.re.paas.internal.fusion;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.SecureMethod;

public class WebSocketServer {

	private static Server server;

	private static final Integer port = 4584;

	
	@SecureMethod
	public static void start() {

		Logger.get().info("Launching embedded web socket server ..");

		server = new Server();
		
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);
        
        // Configure specific websocket behavior
        JettyWebSocketServletContainerInitializer.configure(handler, (servletContext, wsContainer) ->
        {
            // Configure default max size
            wsContainer.setMaxTextMessageSize(65535);

            // Add websockets
            wsContainer.addMapping("/client-session/*", ClientSessionSocketAdapter.class);
        });
 
        
		try {

			server.start();

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}

		Logger.get().info("Web socket server started successfully..");
	}

	@SecureMethod
	public static void stop() {

		Logger.get().info("Stopping web socket server ..");

		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
