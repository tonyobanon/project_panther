package com.re.paas.internal.fusion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import com.re.paas.api.fusion.Part;

class PartImpl implements Part {
	
	private final jakarta.servlet.http.Part jakartaPart;

	public PartImpl(jakarta.servlet.http.Part jakartaPart) {
		this.jakartaPart = jakartaPart;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.jakartaPart.getInputStream();
	}

	@Override
	public String getContentType() {
		return this.jakartaPart.getContentType();
	}

	@Override
	public String getName() {
		return this.jakartaPart.getName();
	}

	@Override
	public String getSubmittedFileName() {
		return this.jakartaPart.getSubmittedFileName();
	}

	@Override
	public long getSize() {
		return this.jakartaPart.getSize();
	}

	@Override
	public void delete() throws IOException {
		this.jakartaPart.delete();
	}

	@Override
	public String getHeader(String name) {
		return this.jakartaPart.getHeader(name);
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return this.jakartaPart.getHeaders(name);
	}

	@Override
	public Collection<String> getHeaderNames() {
		return this.jakartaPart.getHeaderNames();
	}
	
}
