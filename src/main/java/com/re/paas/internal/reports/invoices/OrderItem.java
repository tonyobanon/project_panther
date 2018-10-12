package com.re.paas.internal.reports.invoices;

public class OrderItem {

	private final String name;
	private final String description;
	private final Number price;
	
	public OrderItem(String name, String description, Number price) {
		this.name = name;
		this.description = description;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Number getPrice() {
		return price;
	}
	
}
