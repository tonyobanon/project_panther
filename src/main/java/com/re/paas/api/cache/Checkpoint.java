package com.re.paas.api.cache;

public class Checkpoint {

	private Integer start;
	private Integer end;

	public Checkpoint() {
	}
	
	public Checkpoint(Integer start, Integer end) {
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return start;
	}

	public Checkpoint setStart(Integer start) {
		this.start = start;
		return this;
	}

	public Integer getEnd() {
		return end;
	}

	public Checkpoint setEnd(Integer end) {
		this.end = end;
		return this;
	}

}
