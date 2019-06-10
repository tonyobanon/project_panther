package com.re.paas.internal.billing;

import java.util.Collections;
import java.util.List;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.tasks.TaskImage;
import com.re.paas.internal.classes.TaskInterval;

public class InvoiceSettlementTask extends TaskImage {

	@Override
	public String name() {
		return "invoice_settlement_task";
	}

	@Override
	public ClientRBRef title() {
		return ClientRBRef.forAll("invoice_settlement_task");
	}

	@Override
	public List<AbstractField> fields() {
		return Collections.emptyList();
	}

	@Override
	public TaskExecutionOutcome call() {
		BillingModel.payAllOutstandingInvoices();
		return TaskExecutionOutcome.SUCCESS;
	}
	
	@Override
	public TaskInterval interval() {
		return TaskInterval.MONTHLY;
	}

}
