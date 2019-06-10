package com.re.paas.internal.tables.spec.payments;

public class InvoiceTableSpec {

	public static final String ID = "id";
	public static final String ACCOUNT_ID = "accountId";
	public static final String IS_OUTSTANDING = "isOutstanding";
	public static final String STATUS = "status";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String ITEMS = "items";
	public static final String CURRENCY = "currency";
	public static final String TOTAL_DUE = "totalDue";
	public static final String COMMENT = "comment";
	public static final String DATE_CREATED = "dateCreated";
	public static final String DATE_UPDATED = "dateUpdated";
	
	public static final String ACCOUNT_ID_INDEX = "accountId_index";
	public static final String IS_OUTSTANDING_INDEX = "isOutstanding_index";
	public static final String STATUS_INDEX = "status_index";
	
}
