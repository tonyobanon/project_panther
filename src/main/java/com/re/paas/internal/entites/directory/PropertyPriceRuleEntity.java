package com.re.paas.internal.entites.directory;

import java.util.Date;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.re.paas.internal.classes.spec.ClientSignatureType;

@Cache
@Entity
public class PropertyPriceRuleEntity {

	@Id
	Long id;
	
	Map<ClientSignatureType, String> rules;

	Long propertyId;
	
	Integer operator;
	
	Double percentile;
	
	Double price;

	Double basePrice;
	
	Date dateCreated;
	
	Date dateUpdated;
	
	public Long getId() {
		return id;
	}

	public PropertyPriceRuleEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Map<ClientSignatureType, String> getRules() {
		return rules;
	}

	public PropertyPriceRuleEntity setRules(Map<ClientSignatureType, String> rules) {
		this.rules = rules;
		return this;
	}

	public Long getPropertyId() {
		return propertyId;
	}

	public PropertyPriceRuleEntity setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
		return this;
	}

	public Integer getOperator() {
		return operator;
	}

	public PropertyPriceRuleEntity setOperator(Integer operator) {
		this.operator = operator;
		return this;
	}
	
	public Double getPercentile() {
		return percentile;
	}

	public PropertyPriceRuleEntity setPercentile(Double percentile) {
		this.percentile = percentile;
		return this;
	}

	public Double getPrice() {
		return price;
	}

	public PropertyPriceRuleEntity setPrice(Double price) {
		this.price = price;
		return this;
	}

	public Double getBasePrice() {
		return basePrice;
	}

	public PropertyPriceRuleEntity setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public PropertyPriceRuleEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public PropertyPriceRuleEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}
	
}
