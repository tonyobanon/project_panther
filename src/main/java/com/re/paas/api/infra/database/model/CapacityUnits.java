package com.re.paas.api.infra.database.model;

public class CapacityUnits {

	private final Long readCapacityUnits;
	
	private final Long writeCapacityUnits;

	public CapacityUnits(Long readCapacityUnits, Long writeCapacityUnits) {
		this.readCapacityUnits = readCapacityUnits;
		this.writeCapacityUnits = writeCapacityUnits;
	}

	public Long getReadCapacityUnits() {
		return readCapacityUnits;
	}

	public Long getWriteCapacityUnits() {
		return writeCapacityUnits;
	}
	
}
