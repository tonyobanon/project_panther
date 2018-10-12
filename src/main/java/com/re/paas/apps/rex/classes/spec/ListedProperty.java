package com.re.paas.apps.rex.classes.spec;

import java.util.Date;

public abstract class ListedProperty {
	
	Long id;
	
	Long propertyId;

	Long agentOrganizationId;
	
	Boolean isPremium;

	Boolean isHot;

	PropertyAvailabilityStatus availabilityStatus;

	Date dateCreated;

	Date dateUpdated;

}
