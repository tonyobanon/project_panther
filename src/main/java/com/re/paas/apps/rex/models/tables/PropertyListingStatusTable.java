package com.re.paas.apps.rex.models.tables;

import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.apps.rex.classes.spec.FileAttachmentRef;
import com.re.paas.apps.rex.models.tables.spec.PropertyListingStatusTableSpec;

public class PropertyListingStatusTable implements BaseTable {

	Long id;
	String propertyId;
	Long principal;
	Integer listingStatus;
	String message;
	List<FileAttachmentRef> attachments;
	Date dateCreated;
	
	@Override
	public String hashKey() {
		return PropertyListingStatusTableSpec.ID;
	}
}
