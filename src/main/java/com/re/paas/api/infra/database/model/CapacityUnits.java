package com.re.paas.api.infra.database.model;

public class CapacityUnits {

	private final Double readCapacityUnits;
	
	private final Double writeCapacityUnits;

	public CapacityUnits(Double readCapacityUnits, Double writeCapacityUnits) {
		this.readCapacityUnits = readCapacityUnits;
		this.writeCapacityUnits = writeCapacityUnits;
	}

	public Double getReadCapacityUnits() {
		return readCapacityUnits;
	}

	public Double getWriteCapacityUnits() {
		return writeCapacityUnits;
	}
	
}
