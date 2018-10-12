package com.re.paas.apps.rex.classes.spec;

import java.util.Date;
import java.util.Map;

import com.re.paas.internal.classes.spec.ClientSignatureType;
import com.re.paas.internal.classes.spec.PriceRuleOperator;

public class PropertyPriceRuleSpec {

	Long id;
	
	Map<ClientSignatureType, String> rules;

	Long propertyId;
	
	PriceRuleOperator operator;
	
	Double percentile;
	
	Double price;

	Double basePrice;
	
	Date dateCreated;
	
	Date dateUpdated;
	
	public Long getId() {
		return id;
	}

	public PropertyPriceRuleSpec setId(Long id) {
		this.id = id;
		return this;
	}

	public Map<ClientSignatureType, String> getRules() {
		return rules;
	}

	public PropertyPriceRuleSpec setRules(Map<ClientSignatureType, String> rules) {
		this.rules = rules;
		return this;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public PropertyPriceRuleSpec setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}

	public PriceRuleOperator getOperator() {
		return operator;
	}

	public PropertyPriceRuleSpec setOperator(PriceRuleOperator operator) {
		this.operator = operator;
		return this;
	}

	public Double getPercentile() {
		return percentile;
	}

	public PropertyPriceRuleSpec setPercentile(Double percentile) {
		this.percentile = percentile;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public PropertyPriceRuleSpec setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public PropertyPriceRuleSpec setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyPriceRuleSpec setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyPriceRuleSpec setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
