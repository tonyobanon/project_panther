package com.re.paas.internal.fusion;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.fusion.BaseComponent;
import com.re.paas.api.fusion.Buffer;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.MultiMap;
import com.re.paas.api.utils.JsonParser;
import com.re.paas.internal.runtime.spi.CustomClassLoader;
import com.re.paas.internal.runtime.spi.FusionClassloaders;

import io.netty.handler.codec.http2.DefaultHttp2Headers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpServerResponseImpl implements HttpServerResponse {

	private static final long serialVersionUID = 1L;
	
	HttpServletRequest req;
	HttpServletResponse resp;

	private boolean isEnded;

	private MultiMap headersMap = new Http2HeadersAdaptor(new DefaultHttp2Headers());
	private Map<String, Cookie> cookies = new FluentHashMap<>();

	private int bytesWritten;

	HttpServerResponseImpl(HttpServletRequest request, HttpServletResponse response) {
		this.req = request;
		this.resp = response;
	}

	@Override
	public Map<String, Cookie> cookies() {
		return cookies;
	}

	@Override
	public void addCookie(Cookie cookie) {
		if (ended()) {
			throwResponseEndedException();
		}
		this.resp.addCookie(CookieUtil.toServletCookie(cookie));
		this.cookies.put(cookie.getName(), cookie);
	}

	@Override
	public int getStatusCode() {
		return this.resp.getStatus();
	}

	@Override
	public HttpServerResponse setStatusCode(int statusCode) {
		if (ended()) {
			throwResponseEndedException();
		}
		this.resp.setStatus(statusCode);
		return this;
	}

	@Override
	public MultiMap headers() {
		return headersMap;
	}

	@Override
	public HttpServerResponse putHeader(String name, String value) {
		if (ended()) {
			throwResponseEndedException();
		}
		headersMap.add(name, value);
		this.resp.addHeader(name, value);
		return this;
	}

	public HttpServerResponse write(Buffer chunk) {
		if (ended()) {
			throwResponseEndedException();
		}

		try {
			IOUtils.write(chunk.getBytes(), this.resp.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		bytesWritten += chunk.length();
		return this;
	}

	public HttpServerResponse write(String chunk, String enc) {
		return write(Buffer.buffer(chunk, enc));
	}

	public HttpServerResponse write(String chunk) {
		return write(Buffer.buffer(chunk));
	}

	public void end(String chunk) {
		end(chunk, null);
	}

	public void end(String chunk, String enc) {
		write(chunk, enc);
		end();
	}

	public void end(Buffer chunk) {
		write(chunk);
		end();
	}

	@Override
	public void end() {
		try {
			this.resp.flushBuffer();
			isEnded = true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Boolean isRtlLocale(Locale l) {
		// Todo
		return false;
	}

	@Override
	public HttpServerResponse render(BaseComponent component, Boolean testMode) {

		CustomClassLoader cl = (CustomClassLoader) component.getClass().getClassLoader();

		String appId = cl.getName();

		String assetId = component.getAssetId();

		Boolean rtl = isRtlLocale(req.getLocale());

		String clientHtml = FusionClassloaders.getComponentHtmlClient(appId, assetId);

		// Add RTL setting
		clientHtml = clientHtml.replace("\"{{rtl}}\"", Boolean.toString(rtl));

		// Set testMode
		clientHtml = clientHtml.replace("\"{{testMode}}\"", Boolean.toString(testMode));

		if (!testMode) {

			String data = JsonParser.get().toString(component);

			clientHtml.replace("\"{{data}}\"", data);
		}

		headers().add("Content-Type", "text/html");
		addCookie(new CookieImpl(FusionClassloaders.APP_ID_COOKIE, appId).setPath("/").setMaxAge(84900));

		write(clientHtml);
		return this;
	}

	@Override
	public HttpServerResponse render(BaseComponent component) {
		return render(component, false);
	}

	@Override
	public boolean ended() {
		return isEnded;
	}

	@Override
	public int bytesWritten() {
		return bytesWritten;
	}

	private void throwResponseEndedException() {
		throw new IllegalStateException("The response has already ended, and cannot be written to");
	}
}
