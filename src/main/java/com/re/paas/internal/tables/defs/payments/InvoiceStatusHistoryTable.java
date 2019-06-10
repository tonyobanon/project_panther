package com.re.paas.internal.tables.defs.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.tables.spec.payments.InvoiceStatusHistoryTableSpec;

public class InvoiceStatusHistoryTable implements BaseTable {
	
	Long id;
	
	String invoiceId;
	
	Integer status;
	
	ClientRBRef message;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return InvoiceStatusHistoryTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition invoiceIdIndex = new IndexDefinition(InvoiceStatusHistoryTableSpec.INVOICE_ID_INDEX, Type.GSI)
		.addHashKey(InvoiceStatusHistoryTableSpec.INVOICE_ID);

		indexes.add(invoiceIdIndex);
		
		return indexes;
	}
	
}
