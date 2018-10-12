package com.re.paas.api.forms;

public interface Reference {

	String value();
	
	static Reference empty() {
		return new Reference() {
			
			@Override
			public String value() {
				return "EMPTY";
			}
			
		};
	}

	default boolean equals(Reference obj) {
		return obj.value().equals(value());
	}
	
}
