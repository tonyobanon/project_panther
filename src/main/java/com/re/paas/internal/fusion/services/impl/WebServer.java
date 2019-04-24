package com.re.paas.internal.fusion.services.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.alpn.ALPN;
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http2.HTTP2Cipher;
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

import com.google.common.collect.ImmutableList;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.app_provisioning.AppClassLoader;
import com.re.paas.api.cryto.CryptoAdapter;
import com.re.paas.api.cryto.KeyStoreProperties;
import com.re.paas.api.cryto.SSLContext;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.server.HttpMethod;
import com.re.paas.api.fusion.server.Route;
import com.re.paas.api.fusion.server.RouteHandler;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.ui.AbstractComponent;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.Consumer;
import com.re.paas.api.runtime.ExecutorFactory;
import com.re.paas.api.runtime.Invokable;
import com.re.paas.internal.Platform;
import com.re.paas.internal.runtime.spi.AppProvisioner;

public class WebServer {

	private static Server server;

	@PlatformInternal
	public static void start(ServerOptions options) {

		Logger.get().info("Launching embedded web server ..");

		server = new Server(new InetSocketAddress(options.getHost(), options.getPort()));
		server.setHandler(new WebServer.RequestHandler());

		ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
		alpn.setDefaultProtocol("h2");

		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();

		// HTTPS Configuration
		HttpConfiguration https_config = null;

		// SSL Connection Factory
		SslConnectionFactory ssl = null;

		KeyStoreProperties keyStoreProperties = null;
		SSLContext sslContext;

		if (Platform.isInstalled()
				&& (keyStoreProperties = CryptoAdapter.getDelegate().getProvider().getKeyStoreProperties())
						.keyStoreEnabled()
				&& (sslContext = keyStoreProperties.getSslContext()) != null) {

			http_config.setSecureScheme("https");
			http_config.setSecurePort(options.getSslPort());

			https_config = new HttpConfiguration(http_config);
			https_config.addCustomizer(new SecureRequestCustomizer());

			// SSL Context Factory for HTTPS and HTTP/2
			SslContextFactory sslContextFactory = new SslContextFactory();

			sslContextFactory.setKeyStoreType(keyStoreProperties.getType());

			sslContextFactory.setCertAlias(sslContext.getCertAlias());
			sslContextFactory.setKeyStore(keyStoreProperties.getKeystore());
			sslContextFactory.setKeyStorePassword(keyStoreProperties.getKeyStorePassword());

			sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);

			ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());
		}

		// HTTP/2 Connection Factory
		HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(
				https_config != null ? https_config : http_config);

		// NegotiatingServerConnectionFactory.checkProtocolNegotiationAvailable();

		// HTTP/2 Connector
		ServerConnector http2Connector = new ServerConnector(server, ssl, alpn, h2,
				new HttpConnectionFactory(https_config != null ? https_config : http_config));
		http2Connector.setPort(options.getSslPort());

		server.addConnector(http2Connector);

		ALPN.debug = false;

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Logger.get().info("Server started successfully..");
	}

	@PlatformInternal
	public static Server getServer() {
		return server;
	}

	@PlatformInternal
	public static void stop() {

		Logger.get().info("Stopping embedded web server ..");

		try {
			server.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class RequestHandler extends AbstractHandler {

		public RequestHandler() {
			BufferImpl.setFactory(new BufferFactoryImpl());
		}

		public void handleWeb(String target, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) {

			RoutingContext ctx = new RoutingContextImpl(request, response);
			AbstractComponent.getDelegate().handler(ctx);
		}

		public void handleApi(String target, Request baseRequest, HttpServletRequest request,
				HttpServletResponse response) {

			RoutingContext ctx = new RoutingContextImpl(request, response);

			String path = ctx.request().path().replace(AbstractServiceDelegate.BASE_PATH, "");

			HttpMethod method = ctx.request().method();

			AbstractServiceDelegate serviceDelegate = BaseService.getDelegate();

			// Based on the path, we need to determine the application that owns it
			String appId = path.split("/")[1];

			// Find all matching handlers

			List<RouteHandler> handlers = new ArrayList<>();

			// Add default head handler
			handlers.addAll(ImmutableList.of(Handlers.defaultHeadHandler()));

			// Matching all paths and methods
			handlers.addAll(serviceDelegate.getRouteHandlers(new Route()));

			// Matching only current method
			handlers.addAll(serviceDelegate.getRouteHandlers(new Route().setMethod(method)));

			// Matching only current path
			handlers.addAll(serviceDelegate.getRouteHandlers(new Route().setUri(path)));

			// Matching current path and method
			handlers.addAll(serviceDelegate.getRouteHandlers(new Route().setMethod(method).setUri(path)));

			// Add default tail handler
			handlers.addAll(ImmutableList.of(Handlers.defaultTailHandler()));

			// Recursively call each handler

			for (RouteHandler handler : handlers) {

				// Create exception catching mechanism

				try {

					Consumer<RoutingContext> o = handler.getHandler();

					Invokable<Void> i = () -> {
						try {
							AppClassLoader cl = AppProvisioner.get().getClassloader(appId);
							Class<?> consumerClazz = cl.loadClass(Consumer.class.getName());

							consumerClazz.getDeclaredMethod("accept", Object.class).invoke(o, ctx);
							return null;
							
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					};
					
					ExecutorFactory.get().execute(i);

				} catch (Exception e) {
					ctx.response().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).end(
							com.re.paas.internal.fusion.services.impl.ResponseUtil.toResponse(ErrorHelper.getError(e)));
					break;
				}

				// Check if response is ended

				if (ctx.response().ended()) {
					break;
				}
			}

			if (!ctx.response().ended()) {
				ctx.response().end();
			}
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

			if (path.startsWith(AbstractServiceDelegate.BASE_PATH)) {
				handleApi(target, baseRequest, request, response);
			} else {
				handleWeb(target, baseRequest, request, response);
			}
		}
	}
}
