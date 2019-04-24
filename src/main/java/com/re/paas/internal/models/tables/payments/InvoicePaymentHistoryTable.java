package com.re.paas.internal.models.tables.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.InvoicePaymentHistoryTableSpec;

public class InvoicePaymentHistoryTable implements BaseTable {

	Long id;

	String invoiceId;

	String extReference;

	Integer status;

	ClientRBRef message;

	String additionalInfo;
	
	Integer previousStatus;
	
	Boolean isOverwritten;
	
	Boolean isReconciled;

	Date dateCreated;

	Date dateUpdated;

	@Override
	public String hashKey() {
		return InvoicePaymentHistoryTableSpec.ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition invoiceIdIndex = new IndexDefinition(InvoicePaymentHistoryTableSpec.INVOICE_ID_INDEX, Type.GSI)
		.addHashKey(InvoicePaymentHistoryTableSpec.INVOICE_ID);

		IndexDefinition extReferenceIndex = new IndexDefinition(InvoicePaymentHistoryTableSpec.EXT_REFERENCE_INDEX, Type.GSI)
		.addHashKey(InvoicePaymentHistoryTableSpec.EXT_REFERENCE);
		
		indexes.add(invoiceIdIndex);
		indexes.add(extReferenceIndex);
		
		return indexes;
	}
}
