package com.re.paas.internal.models.tables.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.InvoicePaymentTableSpec;

public class InvoicePaymentTable implements BaseTable {

	Long invoiceId;

	String merchantReference;
	
	String extReference;

	Integer status;

	ClientRBRef message;

	Date dateCreated;

	Date dateUpdated;

	@Override
	public String hashKey() {
		return InvoicePaymentTableSpec.INVOICE_ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition extReferenceIndex = new IndexDefinition(InvoicePaymentTableSpec.EXT_REFERENCE_INDEX, Type.GSI)
		.addHashKey(InvoicePaymentTableSpec.EXT_REFERENCE);
		
		indexes.add(extReferenceIndex);
		
		return indexes;
	}
}
