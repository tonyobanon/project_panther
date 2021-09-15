package com.re.paas.api.events;

import com.re.paas.api.errors.Error;

public enum EventError implements Error {

	ASYNC_EVENTS_ARE_DISABLED(5, "Async events are disabled for {ref1}", false);	
	
	private boolean isFatal;
	private int code;
	private String message;

	private EventError(Integer code, String message) {
		this(code, message, false);
	}
	
	private EventError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

    public String namespace() {
    	return "event";
    }
	  
	public static EventError from(int value) {

		switch (value) {
		
		case 5:
			return EventError.ASYNC_EVENTS_ARE_DISABLED;
			
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
