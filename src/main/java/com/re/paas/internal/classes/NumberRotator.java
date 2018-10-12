package com.re.paas.internal.classes;

public class NumberRotator {

	private final short start;
	private final short end;
	
	private short current;

	/**
	 * @param start start number (inclusive)
	 * @param end end number (exclusive)
	 */
	public NumberRotator(int start, int end) {
		assert start < end;
		this.start = (short) start;
		this.end = (short) end;
		this.reset();
	}
	
	private void reset() {
		this.current = (short) (this.start - 1);
	}
	
	public synchronized int next() {
		
		short current = ++ this.current;
		
		if(current >= end) {
			reset();
			return next();
		}
		
		return current;
	}
	
	public boolean isWithinRange(short number) {
		return number >= start && number < end;
	}

}
