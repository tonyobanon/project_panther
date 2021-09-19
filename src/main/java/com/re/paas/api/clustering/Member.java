package com.re.paas.api.clustering;

import java.io.Serializable;
import java.net.InetSocketAddress;

public class Member implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Short id;
	private final String name;
	private final InetSocketAddress host;
	
	public Member(Short id, String name, InetSocketAddress host) {
		this.id = id;
		this.name = name;
		this.host = host;
	}

	public Short getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public InetSocketAddress getHost() {
		return host;
	}

}
	

