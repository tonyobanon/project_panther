package com.re.paas.api.fusion;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.H2TlsStrategy;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.ShutdownType;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;

import com.re.paas.api.classes.Exceptions;

public class AsyncSSLClient {

	private CloseableHttpAsyncClient client;
	private SimpleHttpRequest req = SimpleHttpRequest.get("/");

	private FutureCallback<SimpleHttpResponse> future = new FutureCallback<SimpleHttpResponse>() {
		@Override
		public void failed(Exception ex) {
		}

		@Override
		public void completed(SimpleHttpResponse result) {
		}

		@Override
		public void cancelled() {
		}
	};

	private Credentials credentials;
	private CredentialsProvider credentialsProvider;

	public AsyncSSLClient(String domain) {

		// Create Client

		try {

			// Trust standard CA and those trusted by our custom strategy
			final SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {

				@Override
				public boolean isTrusted(final X509Certificate[] chain, final String authType)
						throws CertificateException {
					final X509Certificate cert = chain[0];
					return ("CN=" + domain).equalsIgnoreCase(cert.getSubjectDN().getName());
				}

			}).setProtocol("TLSv1.2").build();

			final TlsStrategy tlsStrategy = new H2TlsStrategy(sslcontext, H2TlsStrategy.getDefaultHostnameVerifier()) {

				// IMPORTANT
				// In order for HTTP/2 protocol negotiation to succeed one must
				// allow access
				// to Java 9 specific properties of SSLEngine via reflection
				// by adding the following line to the JVM arguments
				//
				// --add-opens java.base/sun.security.ssl=ALL-UNNAMED
				//
				// or uncomment the method below

				// @Override
				// protected TlsDetails createTlsDetails(final SSLEngine
				// sslEngine) {
				// return new TlsDetails(sslEngine.getSession(),
				// sslEngine.getApplicationProtocol());
				// }

			};

			final PoolingAsyncClientConnectionManager cm = PoolingAsyncClientConnectionManagerBuilder.create()
					.setTlsStrategy(tlsStrategy).build();

			this.client = HttpAsyncClients.custom().setConnectionManager(cm).build();

		} catch (RuntimeException | KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			Exceptions.throwRuntime(e);
		}
	}

	public AsyncSSLClient setReq(SimpleHttpRequest req) {
		this.req = req;
		return this;
	}

	public AsyncSSLClient setFuture(FutureCallback<SimpleHttpResponse> future) {
		this.future = future;
		return this;
	}

	public AsyncSSLClient setCredentialsProvider(CredentialsProvider credentialsProvider) {
		this.credentialsProvider = credentialsProvider;
		return this;
	}
	
	public AsyncSSLClient setCredentials(Credentials credentials) {
		this.credentials = credentials;
		return this;
	}

	public void execute() {
		client.start();

		HttpClientContext context = HttpClientContext.create();
		
		if(credentialsProvider != null){
			context.setCredentialsProvider(credentialsProvider);
		} else if (credentials != null) {
			context.setCredentialsProvider(new CredentialsProvider() {
				@Override
				public Credentials getCredentials(AuthScope authscope, HttpContext context) {
					return credentials;
				}
			});
		}

		client.execute(req, future);

		client.shutdown(ShutdownType.IMMEDIATE);
	}

}
