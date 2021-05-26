package com.re.paas.api.infra.database.model;

public class ProvisionedThroughput {

	private final Long readCapacityUnits;
	
	private final Long writeCapacityUnits;

	public ProvisionedThroughput(Long readCapacityUnits, Long writeCapacityUnits) {
		super();
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
