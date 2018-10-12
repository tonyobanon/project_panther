package com.re.paas.api.errors;

/**
 * All enums implementing this interface should at least contain a static method
 * from(int)
 */
public interface Error {

	public int getCode();

	public String getMessage();

	public boolean isFatal();
	
	public String namespace();

}
