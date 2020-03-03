package com.re.paas.integrated.tables.defs.payments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.re.paas.api.infra.database.model.IndexDefinition;
import com.re.paas.api.infra.database.model.IndexDescriptor.Type;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.integrated.tables.spec.payments.BillingContextTableSpec;

public class BillingContextTable implements BaseTable {

	Long accountId;

	String invoiceId;

	Integer status;

	String currency;

	BigDecimal totalDue;

	Date dateUpdated;
	
	@Override
	public String hashKey() {
		return BillingContextTableSpec.ACCOUNT_ID;
	}
	
	@Override
	public List<IndexDefinition> indexes() {
		List<IndexDefinition> indexes = new ArrayList<>();
		
		IndexDefinition invoiceIdIndex = new IndexDefinition(BillingContextTableSpec.INVOICE_ID_INDEX, Type.GSI)
		.addHashKey(BillingContextTableSpec.INVOICE_ID);
		
		indexes.add(invoiceIdIndex);
		
		return indexes;
	}
	
}
