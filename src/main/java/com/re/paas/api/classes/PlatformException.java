package com.re.paas.api.classes;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.errors.Error;

@BlockerTodo("Instead of ref1, ref2, make ref dynamic, and use the spread operator")
public class PlatformException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final Error error;

	private Object ref1;
	private Object ref2;
	
	private Object data;

	public PlatformException(Error reason) {
		this(reason, null);
	}

	public PlatformException(Error reason, String ref1) {
		this(reason, ref1, null);
	}

	public PlatformException(Error reason, String ref1, String ref2) {
		super(reason.getMessage());
		this.error = reason;
		this.ref1 = ref1;
		this.ref2 = ref2;
	}

	public static PlatformException get(Error reason) {
		return get(reason, null);
	}

	public static PlatformException get(Error reason, Object ref1) {
		return get(reason, ref1, null);
	}

	public static PlatformException get(Error reason, Object ref1, Object ref2) {
		return new PlatformException(reason, ref1 != null ? ref1.toString() : null,
				ref2 != null ? ref2.toString() : null);
	}

	public Object getRef1() {
		return ref1;
	}

	public PlatformException setRef1(Object ref1) {
		this.ref1 = ref1;
		return this;
	}

	public Object getRef2() {
		return ref2;
	}

	public PlatformException setRef2(Object ref2) {
		this.ref2 = ref2;
		return this;
	}

	public Error getError() {
		return error;
	}

	public Object getData() {
		return data;
	}

	public PlatformException setData(Object data) {
		this.data = data;
		return this;
	}

	@Override
	public String getMessage() {

		String msg = super.getMessage();

		if (getRef1() != null) {
			msg.replace("{ref1}", getRef1().toString());
		}

		if (getRef2() != null) {
			msg.replace("{ref2}", getRef2().toString());
		}
		return msg;
	}

}
