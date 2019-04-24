package com.re.paas.apps.rex.models.tables;

import java.util.Date;
import java.util.Map;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.models.tables.spec.PropertyPriceRuleTableSpec;
import com.re.paas.internal.classes.spec.ClientSignatureType;

public class PropertyPriceRuleTable implements BaseTable {

	Long id;
	
	Map<ClientSignatureType, String> rules;

	Long propertyId;
	
	Integer operator;
	
	Double percentile;
	
	Double price;

	Double basePrice;
	
	Date dateCreated;
	
	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return PropertyPriceRuleTableSpec.ID;
	}
}
