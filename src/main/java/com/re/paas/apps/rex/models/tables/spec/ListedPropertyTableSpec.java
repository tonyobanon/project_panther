package com.re.paas.apps.rex.models.tables.spec;

public class ListedPropertyTableSpec {

	public static final String ID = "id";
	public static final String PROPERTY_ID = "propertyId";
	public static final String AGENT_ORGANIZATION = "agentOrganization";
	public static final String IS_PREMIUM = "isPremium";
	public static final String IS_HOT = "isHot";
	public static final String AVAILABILITY_STATUS = "availabilityStatus";
	public static final String DATE_CREATED = "dateCreated";
	public static final String DATE_UPDATED = "dateUpdated";
	
	public static final String CONTRACT_TYPE = "contractType";
	
	public static final String PAYMENT_OPTION = "paymentOption"; // PropertyContractType.SALE
	public static final String YEARS_REQUIRED = "yearsRequired";// PropertyContractType.RENT
	

	public static final String AGENT_ORGANIZATION_INDEX = "agentOrganization_index";
	public static final String CONTRACT_TYPE_INDEX = "contractType_index";
	
	public static final String PAYMENT_OPTION_INDEX = "paymentOption_index"; // Used in Property Search
	public static final String YEARS_REQUIRED_INDEX = "yearsRequired_index"; // Used in Property Search
	
}
