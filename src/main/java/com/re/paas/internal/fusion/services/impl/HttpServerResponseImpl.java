package com.re.paas.internal.fusion.services.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.fusion.server.Buffer;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.CookieUtil;
import com.re.paas.api.fusion.server.HttpServerResponse;
import com.re.paas.api.fusion.server.MultiMap;

import io.netty.handler.codec.http2.DefaultHttp2Headers;

public class HttpServerResponseImpl implements HttpServerResponse {

	HttpServletResponse resp;

	private boolean isEnded;

	private MultiMap headersMap = new Http2HeadersAdaptor(new DefaultHttp2Headers());
	private Map<String, Cookie> cookies = new FluentHashMap<>();

	private int bytesWritten;

	HttpServerResponseImpl(HttpServletResponse response) {
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

	@Override
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

	@Override
	public HttpServerResponse write(String chunk, String enc) {
		return write(Buffer.buffer(chunk, enc));
	}

	@Override
	public HttpServerResponse write(String chunk) {
		return write(Buffer.buffer(chunk));
	}

	@Override
	public void end(String chunk) {
		end(chunk, null);
	}

	@Override
	public void end(String chunk, String enc) {
		write(ResponseUtil.toResponse(getStatusCode(), chunk), enc);
		end();
	}

	@Override
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
