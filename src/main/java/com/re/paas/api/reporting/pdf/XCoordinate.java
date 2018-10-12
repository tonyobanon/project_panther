package com.re.paas.api.reporting.pdf;

public class XCoordinate {

	private float start;
	private float stop;
	
	public XCoordinate() {
	}
	
	public XCoordinate(float start, float stop) {
		this.start = start;
		this.stop = stop;
	}
	
	public float getStart() {
		return start;
	}
	
	public void setStart(float start) {
		this.start = start;
	}
	
	public float getStop() {
		return stop;
	}
	
	public void setStop(float stop) {
		this.stop = stop;
	}
	
}
