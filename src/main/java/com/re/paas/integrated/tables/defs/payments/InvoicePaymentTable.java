package com.re.paas.integrated.tables.defs.payments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.payments.InvoicePaymentTableSpec;

public class InvoicePaymentTable implements BaseTable {

	Long invoiceId;

	String reference;
	
	String gatewayReference;

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
		
		IndexDefinition referenceIndex = new IndexDefinition(InvoicePaymentTableSpec.REFERENCE_INDEX, Type.GSI)
		.addHashKey(InvoicePaymentTableSpec.REFERENCE);
		
		indexes.add(referenceIndex);
		
		return indexes;
	}
}
