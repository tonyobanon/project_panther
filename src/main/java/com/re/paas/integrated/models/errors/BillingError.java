package com.re.paas.integrated.models.errors;

import com.re.paas.api.errors.Error;

public enum BillingError implements Error {

	AUTH_HEADER_NOT_FOUND(5, "Access denied"),	
	PREVIOUS_INVOICE_IS_OUTSTANDING(10, "Unable to create new invoice. Previous invoice is outstanding"),
	CURRENT_INVOICE_STATUS_CANNOT_BE_UPDATED(15, "Unable to update invoice. Current invoice status cannot be updated"),
	NO_AMOUNT_ON_INVOICE_ITEM(20, "Unable to update invoice. No amount was found on invoice item");
	
	
	private boolean isFatal;
	private int code;
	private String message;

	private BillingError(Integer code, String message) {
		this(code, message, false);
	}
	
	private BillingError(Integer code, String message, boolean isFatal) {
		this.code = code;
		this.message = message;
		this.isFatal = isFatal;
	}

	@Override
    public String namespace() {
    	return "billing";
    }
	  
	public static BillingError from(int value) {

		switch (value) {
		
		case 5:
			return BillingError.AUTH_HEADER_NOT_FOUND;			
		case 10:
			return BillingError.PREVIOUS_INVOICE_IS_OUTSTANDING;			
		case 15:
			return BillingError.CURRENT_INVOICE_STATUS_CANNOT_BE_UPDATED;
		case 20:
			return BillingError.NO_AMOUNT_ON_INVOICE_ITEM;
			
		default:
			return null;			
		}
	}
	
	@Override
	public boolean isFatal() {
		return isFatal;
	}
	
	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
