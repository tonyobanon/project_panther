package com.re.paas.internal.tables.defs.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.payments.InvoiceTableSpec;

public class InvoiceTable implements BaseTable {

	Long id;
	
	String accountId;
	
	Boolean isOutstanding;
	
	Integer status;
	
	Date startDate;
	
	Date endDate;
	
	List<Long> items;
	
	String currency;
	
	Double totalDue;
	
	ClientRBRef comment;
	
	Date dateCreated;

	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return InvoiceTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition accountIdIndex = new IndexDefinition(InvoiceTableSpec.ACCOUNT_ID_INDEX, Type.GSI)
		.addHashKey(InvoiceTableSpec.ACCOUNT_ID);

		IndexDefinition isOutstandingIndex = new IndexDefinition(InvoiceTableSpec.IS_OUTSTANDING_INDEX, Type.GSI)
		.addHashKey(InvoiceTableSpec.IS_OUTSTANDING).setQueryOptimzed(true);
		
		IndexDefinition statusIndex = new IndexDefinition(InvoiceTableSpec.STATUS_INDEX, Type.GSI)
				.addHashKey(InvoiceTableSpec.STATUS).setQueryOptimzed(true);
		
		indexes.add(accountIdIndex);
		indexes.add(isOutstandingIndex);
		indexes.add(statusIndex);
		
		return indexes;
	}
}
