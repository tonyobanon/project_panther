package com.re.paas.api.clustering;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import com.re.paas.api.clustering.classes.MemberStatus;
import com.re.paas.api.utils.Dates;

public class Member implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Short memberId;
	private MemberStatus status;
	
	private final InetSocketAddress host;
	
	private final Date joinDate;
	private List<String> roles;
	
	public Member(Short memberId, InetSocketAddress host) {
		
		this.memberId = memberId;
		this.status = MemberStatus.STARTING;
		
		this.host = host;
		
		this.joinDate = Dates.now();
	}

	public Short getMemberId() {
		return memberId;
	}

	public InetSocketAddress getHost() {
		return host;
	}
	
	public Date getJoinDate() {
		return joinDate;
	}
	
	public List<String> getRoles() {
		return roles;
	}

	public void addRole(String role) {
		this.roles.add(role);
	}

	public MemberStatus getStatus() {
		return status;
	}

	public Member setStatus(MemberStatus status) {
		this.status = status;
		return this;
	}
	
}
