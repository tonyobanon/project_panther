package com.re.paas.internal.billing;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.forms.Section;
import com.re.paas.api.tasks.Task;
import com.re.paas.api.tasks.TaskModel;

public class InvoiceSettlementTaskModel extends TaskModel {

	@Override
	public String name() {
		return "invoice_settlement";
	}

	@Override
	public ClientRBRef title() {
		return ClientRBRef.forAll("invoice_settlement");
	}

	@Override
	public List<Section> fields() {
		return null;
	}	

	@Override
	public Task build(Map<String, Object> parameters) {
		
		return new Task() {
			
			@Override
			public String interval() {
				// return TaskInterval.MONTHLY;
				return "monthly";
			}
			
			@Override
			public String id() {
				return "invoice_settlement_task";
			}
			
			@Override
			public TaskExecutionOutcome call() {
				BillingModel.payAllOutstandingInvoices();
				return TaskExecutionOutcome.SUCCESS;
			}
		};
	}
	
	@Override
	public Map<String, Object> defaultParameters() {
		return Collections.emptyMap();
	}
}
