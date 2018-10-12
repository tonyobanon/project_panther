package com.re.paas.api.classes;

import com.re.paas.api.annotations.Todo;

public class ResourceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static final int RESOURCE_ALREADY_EXISTS = 0;
	public static final int RESOURCE_NOT_FOUND = 1;
	
	public static final int FAILED_VALIDATION = 2;
	public static final int UPDATE_NOT_ALLOWED = 3;
	
	public static final int DELETE_NOT_ALLOWED = 4;
	public static final int RESOURCE_STILL_IN_USE = 5;
	
	public static final int ACCESS_NOT_ALLOWED = 6;
	

	private final int errCode;
	private final String ref;

	public ResourceException(int errCode) {
		this.errCode = errCode;
		this.ref = null;
	}
	
	public ResourceException(int errCode, String ref) {
		this.errCode = errCode;
		this.ref = ref;
	}

	public int getErrCode() {
		return errCode;
	}

	public String getRef() {
		return ref;
	}
	
	@Override
	@Todo("Refractor this please")
	public String getMessage() {
		
		switch (errCode) {
		
		case RESOURCE_ALREADY_EXISTS:
			return "Resource already exists" + ref != null ? " (" + ref + ")" : "";
			
		case RESOURCE_NOT_FOUND:
			return "Resource not found" + ref != null ? " (" + ref + ")" : "";
			
		case FAILED_VALIDATION:
			return "Failed validation" + ref != null ? " (" + ref + ")" : "";
			
		case UPDATE_NOT_ALLOWED:
			return "Update not allowed" + ref != null ? " (" + ref + ")" : "";
			
		case DELETE_NOT_ALLOWED:
			return "Delete not allowed" + ref != null ? " (" + ref + ")" : "";
			
		case RESOURCE_STILL_IN_USE:
			return "Resource still in use" + ref != null ? " (" + ref + ")" : "";
			
		case ACCESS_NOT_ALLOWED:
			return "Access not allowed" + ref != null ? " (" + ref + ")" : "";

		default:
			return super.getMessage();
		}
		
	}
}
