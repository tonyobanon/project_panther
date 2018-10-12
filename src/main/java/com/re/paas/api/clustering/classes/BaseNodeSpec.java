package com.re.paas.api.clustering.classes;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;

import com.re.paas.api.clustering.NodeRole;

public class BaseNodeSpec implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Short id;
	private String name;
	private InetAddress remoteAddress;
	private Integer inboundPort;
	private NodeState state;
	private Collection<NodeRole> roles;
	private Date joinDate;

	public Short getId() {
		return id;
	}

	public BaseNodeSpec setId(Short id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public BaseNodeSpec setName(String name) {
		this.name = name;
		return this;
	}

	public InetAddress getRemoteAddress() {
		return remoteAddress;
	}

	public BaseNodeSpec setRemoteAddress(InetAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
		return this;
	}

	public Integer getInboundPort() {
		return inboundPort;
	}

	public BaseNodeSpec setInboundPort(Integer inboundPort) {
		this.inboundPort = inboundPort;
		return this;
	}

	public NodeState getState() {
		return state;
	}

	public BaseNodeSpec setState(NodeState state) {
		this.state = state;
		return this;
	}
	
	public Collection<NodeRole> getRoles() {
		return roles;
	}

	public BaseNodeSpec setRoles(Collection<NodeRole> roles) {
		this.roles = roles;
		return this;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public BaseNodeSpec setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
		return this;
	}
}
