package com.re.paas.apps.rex.classes.spec;

import com.re.paas.api.listable.ListingFilter;

public abstract class PropertyListingRequest {

	private PropertyContractType contractType;

	public PropertyContractType getContractType() {
		return contractType;
	}

	public PropertyListingRequest setContractType(PropertyContractType contractType) {
		this.contractType = contractType;
		return this;
	}
	
	protected ListingFilter getDefaultListingFilter(){
		return new ListingFilter("contractType", contractType.getValue());
	}
	
	public abstract ListingFilter getListingFilter();	
}
