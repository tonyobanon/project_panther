package com.re.paas.internal.fusion;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.google.common.base.Joiner;
import com.re.paas.api.Activator;
import com.re.paas.api.Platform;
import com.re.paas.api.Platform.State;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.crytography.CryptoAdapter;
import com.re.paas.api.crytography.KeyStoreProperties;
import com.re.paas.api.crytography.SSLContext;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpStatusCodes;
import com.re.paas.api.fusion.Route;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.StaticFileContext;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.internal.runtime.spi.FusionClassloaders;

@BlockerTodo("Optimize the way requests are handled, as the current impl is thread expensive.")
public class WebServer {

	private static Server server;

	// The port on the node through which the service available through
	// https://matthewpalmer.net/kubernetes-app-developer/articles/kubernetes-ports-targetport-nodeport-service.html
	
	private static final Integer serviceHttpPort = 8082;
	private static final Integer serviceHttpsPort = 8433;

	
	@SecureMethod
	public static void start() {

		Logger.get().info("Launching embedded web server ..");

		// Create and configure a ThreadPool.
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setName("server");

		server = new Server(threadPool);

		ServerConnector connector = null;

		KeyStoreProperties keyStoreProperties = null;
		SSLContext sslContext;

		if (Activator.get().isInstalled()
				&& (keyStoreProperties = CryptoAdapter.getDelegate().getProvider().getKeyStoreProperties())
						.keyStoreEnabled()
				&& (sslContext = keyStoreProperties.getSslContext()) != null) {

			// The HTTP configuration object.
			HttpConfiguration httpConfig = new HttpConfiguration();
			// Add the SecureRequestCustomizer because we are using TLS.
			httpConfig.addCustomizer(new SecureRequestCustomizer());

			// The ConnectionFactory for HTTP/1.1.
			HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);

			// The ConnectionFactory for HTTP/2.
			HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpConfig);

			// The ALPN ConnectionFactory.
			ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
			// The default protocol to use in case there is no negotiation.
			alpn.setDefaultProtocol(http11.getProtocol());

			ALPN.debug = true;

			// Configure the SslContextFactory with the keyStore information.
			SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();

			sslContextFactory.setKeyStoreType(keyStoreProperties.getType());

			sslContextFactory.setCertAlias(sslContext.getCertAlias());
			sslContextFactory.setKeyStore(keyStoreProperties.getKeystore());
			sslContextFactory.setKeyStorePassword(keyStoreProperties.getKeyStorePassword());

			// The ConnectionFactory for TLS.
			SslConnectionFactory tls = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

			// The ServerConnector instance.
			connector = new ServerConnector(server, tls, alpn, h2, http11);
			connector.setPort(serviceHttpsPort);

		} else {

			// Create the ServerConnector.
			connector = new ServerConnector(server);
			connector.setPort(serviceHttpPort);
		}

		server.addConnector(connector);

		server.setHandler(new WebServer.RequestHandler());
        
		try {

			server.start();

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}

		Logger.get().info("Web server started successfully..");
	}

	@SecureMethod
	public static void stop() {

		Logger.get().info("Stopping web server ..");

		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class RequestHandler extends AbstractHandler {

		public RequestHandler() {
			
		}
		
		@Override
		public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
				jakarta.servlet.http.HttpServletResponse response)
				throws IOException, jakarta.servlet.ServletException {
			
			try {
			handle0(target, baseRequest, request, response);
			} catch (Exception e) {
				e.printStackTrace();
				Exceptions.throwRuntime(e);
			}
		}
		
		private void handle0(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
				jakarta.servlet.http.HttpServletResponse response)
				throws IOException, jakarta.servlet.ServletException {

			baseRequest.setHandled(true);

			String reqPath = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

			if (reqPath.equals("/")) {

				response.setStatus(HttpStatusCodes.SC_MOVED_PERMANENTLY);
				response.addHeader("Location", "/platform");

				return;
			}

			Deque<String> parts = new LinkedList<>();

			for (String p : reqPath.split("/")) {
				if (!p.isEmpty()) {
					parts.addLast(p);
				}
			}

			if (ClassLoaders.getClassLoader(parts.peekFirst()) != null) {

				String appId = parts.removeFirst();

				if (Platform.getState(appId) != State.RUNNING) {

					response.setStatus(HttpStatusCodes.SC_SERVICE_UNAVAILABLE);
					return;
				}

				String path = "/" + Joiner.on('/').join(parts);

				Route route = new Route(appId, path, HttpMethod.valueOf(request.getMethod()));

				RoutingContext ctx = new RoutingContextImpl(
						route, request, response, 
						/**
						 * Session support depends a fully configured CacheAdapter
						 */
						Activator.get().isInstalled()
				);

				BaseService.getDelegate().handler(ctx);

				return;

			} else {

				String appId = CookieHelper.getCookie(request.getCookies(), FusionClassloaders.APP_ID_COOKIE);

				if (appId == null) {
					response.setStatus(HttpStatusCodes.SC_NOT_FOUND);
					return;
				}

				String staticPath = Joiner.on(File.separatorChar).join(parts);
				
				StaticFileContext ctx = new StaticFileContextImpl(appId, staticPath, request, response);
				
				BaseService.getDelegate().handler(ctx);
			}
		}

	}

}
