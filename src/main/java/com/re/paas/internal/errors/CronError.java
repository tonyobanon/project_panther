package com.re.paas.internal.errors;

import com.re.paas.api.errors.Error;

public enum CronError implements Error {

	CANNOT_DELETE_INTERNAL_CRON_JOB(5, "Cannot delete an internal cron job");
	
	private boolean isFatal;
	private int code;
	private String message;

	private CronError(Integer code, String message) {
		this(code, message, false);
	}
	
	private CronError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
    public String namespace() {
    	return "cron";
    }
	  
	public static CronError from(int value) {

		switch (value) {
		
		case 5:
			return CronError.CANNOT_DELETE_INTERNAL_CRON_JOB;
			
		default:
			return null;			
		}
	}
	
	@Override
	public boolean isFatal() {
		return isFatal;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
