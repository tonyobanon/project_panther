package com.re.paas.api.fusion.services;

import com.re.paas.api.classes.ResourceException;

public class ServiceException extends ResourceException {

	public ServiceException(int errCode) {
		super(errCode);
	}
	
	public ServiceException(int errCode, String msg) {
		super(errCode, msg);
	}

	private static final long serialVersionUID = 1L;
	
}
