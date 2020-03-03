package com.re.paas.internal.fusion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

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

import com.re.paas.api.Activator;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.cryto.CryptoAdapter;
import com.re.paas.api.cryto.KeyStoreProperties;
import com.re.paas.api.cryto.SSLContext;
import com.re.paas.api.fusion.RoutingContext;
import com.re.paas.api.fusion.assets.AbstractClientAssetDelegate;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.fusion.services.BaseService;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.networking.InetAddressResolver;
import com.re.paas.api.runtime.SecureMethod;

@BlockerTodo("Optimize the way requests are handled, as the current impl is thread expensive.")
public class WebServer {

	private static Server server;

	// The port on the node through which the service available through
	// https://matthewpalmer.net/kubernetes-app-developer/articles/kubernetes-ports-targetport-nodeport-service.html
	private static final Integer serviceHttpPort = 80;
	private static final Integer serviceHttpsPort = 433;

	@SecureMethod
	public static void start() {

		Logger.get().info("Launching embedded web server ..");
		
		InetAddress host = InetAddressResolver.get().getInetAddress();

		server = new Server(new InetSocketAddress(host, serviceHttpPort));
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

		if (Activator.get().isInstalled()
				&& (keyStoreProperties = CryptoAdapter.getDelegate().getProvider().getKeyStoreProperties())
						.keyStoreEnabled()
				&& (sslContext = keyStoreProperties.getSslContext()) != null) {

			http_config.setSecureScheme("https");
			http_config.setSecurePort(serviceHttpsPort);

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
		http2Connector.setPort(serviceHttpsPort);

		server.addConnector(http2Connector);

		ALPN.debug = false;

		try {

			server.start();
			server.join();

		} catch (Exception e) {
			Exceptions.throwRuntime(e);
		}

		Logger.get().info("Server started successfully..");
	}

	@SecureMethod
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

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {

			String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

			RoutingContext ctx = new RoutingContextImpl(request, response,
					path.equals(AbstractServiceDelegate.BASE_PATH));

			switch (path) {

			case AbstractClientAssetDelegate.BASE_PATH:
				
				// This is still being fleshed out
				// ClientAsset.getDelegate().handler(ctx);
				
				ctx.response().setStatusCode(HttpServletResponse.SC_OK);
				break;

			case AbstractServiceDelegate.BASE_PATH:
				BaseService.getDelegate().handler(ctx);
				break;
			}
		}
	}

}
