package com.re.paas.internal.fusion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.fusion.Buffer;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.FileUpload;
import com.re.paas.api.fusion.HttpMethod;
import com.re.paas.api.fusion.HttpServerRequest;
import com.re.paas.api.fusion.MultiMap;

import io.netty.handler.codec.http2.DefaultHttp2Headers;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class HttpServerRequestImpl implements HttpServerRequest {

	private HttpServletRequest req;

	protected MultiMap headersMap = new Http2HeadersAdaptor(new DefaultHttp2Headers());
	protected MultiMap paramsMap = new Http2HeadersAdaptor(new DefaultHttp2Headers());
	protected MultiMap formAttributesMap = new Http2HeadersAdaptor(new DefaultHttp2Headers());

	Buffer body;

	private String path;
	private Boolean isSSL;
	private HttpMethod method;
	private String scheme;
	private InetSocketAddress localAddr;
	private InetSocketAddress remoteAddr;
	private Map<String, Cookie> cookies = new FluentHashMap<>();
	private Collection<FileUpload> fileUploads = new FluentArrayList<>();

	private List<Locale> locales = new ArrayList<>();

	HttpServerRequestImpl(HttpServletRequest request) {

		String path = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");

		this.req = request;

		// Add request headers

		Enumeration<String> headerNames = request.getHeaderNames();

		while (headerNames.hasMoreElements()) {
			String o = headerNames.nextElement();
			this.headersMap.add(o, request.getHeader(o));
		}

		// Add request parameters

		request.getParameterMap().forEach((k, v) -> {
			this.paramsMap.set(k, v);
		});

		// Add form attributes

		Enumeration<String> formAttributeNames = request.getAttributeNames();

		while (formAttributeNames.hasMoreElements()) {
			String o = formAttributeNames.nextElement();
			this.formAttributesMap.add(o, req.getAttribute(o).toString());
		}

		// Add extra information

		this.path = path;
		this.isSSL = request.isSecure();
		this.method = HttpMethod.valueOf(request.getMethod());
		this.scheme = request.getScheme();

		this.localAddr = new InetSocketAddress(request.getLocalName(), request.getLocalPort());
		this.remoteAddr = new InetSocketAddress(request.getRemoteHost(), request.getRemotePort());

		
		// Add cookies

		if (request.getCookies() != null) {
			for (jakarta.servlet.http.Cookie c : request.getCookies()) {
				cookies.put(c.getName(), CookieUtil.toFusionCookie(c));
			}
		}

		// Add request body
		try {
			
			this.body = Buffer.buffer(IOUtils.toByteArray(request.getInputStream()));
		
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		// Add multi-part data, if available

		if (request.getContentType() != null && request.getContentType().equals("multipart/form-data")) {

			try {
				Collection<Part> parts = request.getParts();

				for (Part part : parts) {
					fileUploads.add(new FileUploadImpl(part.getInputStream()));
				}
			} catch (IOException | ServletException e) {
				Exceptions.throwRuntime(e);
			}

		}
		
		// Add locales

		Enumeration<Locale> locales = request.getLocales();
		
		while (locales.hasMoreElements()) {
			Locale locale = locales.nextElement();
			this.locales.add(locale);
		}
	}
	
	@Override
	public Map<String, Cookie> cookies() {
		return cookies;
	}

	@Override
	public Buffer body() {
		return body;
	}

	@Override
	public Collection<FileUpload> fileUploads() {
		return fileUploads;
	}

	@Override
	public List<Locale> locales() {
		return locales;
	}

	@Override
	public HttpMethod method() {
		return method;
	}

	@Override
	public String rawMethod() {
		return method.name();
	}

	@Override
	public boolean isSSL() {
		return isSSL;
	}

	@Override
	public String scheme() {
		return scheme;
	}

	@Override
	public String path() {
		return path;
	}

	@Override
	public String query() {
		return req.getQueryString();
	}

	@Override
	public String host() {
		return remoteAddress().getHostName();
	}

	@Override
	public MultiMap headers() {
		return headersMap;
	}

	@Override
	public String getHeader(String headerName) {
		return headers().get(headerName);
	}

	@Override
	public MultiMap params() {
		return paramsMap;
	}

	@Override
	public String getParam(String paramName) {
		return  params().get(paramName);
	}
	
	@Override
	public MultiMap formAttributes() {
		return formAttributesMap;
	}

	@Override
	public CharSequence getFormAttribute(String attributeName) {
		return formAttributes().get(attributeName);
	}

	@Override
	public InetSocketAddress remoteAddress() {
		return remoteAddr;
	}

	@Override
	public InetSocketAddress localAddress() {
		return localAddr;
	}
}
