package com.re.paas.internal.models.tables.payments;

import java.util.Date;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.InvoiceItemTableSpec;

public class InvoiceItemTable implements BaseTable {

	Long id;

	ClientRBRef name;
	
	ClientRBRef description;

	Double amount;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return InvoiceItemTableSpec.ID;
	}

}
