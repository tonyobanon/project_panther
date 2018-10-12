package com.re.paas.internal.entites.directory;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class ListedPropertyEntity {

	@Id
	public Long id;
	
	protected Long propertyId;
	
	@Index
	public Long agentOrganizationId;
	
	protected Boolean isPremium;

	protected Boolean isHot;

	public Integer availabilityStatus;
	
	protected Date dateCreated;

	protected Date dateUpdated;

}
