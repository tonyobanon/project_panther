package com.re.paas.internal.models.tables.payments;

import java.util.Date;

import com.re.paas.api.infra.database.modelling.BaseTable;
import com.re.paas.internal.models.tables.spec.CardTableSpec;

public class CardTable implements BaseTable {

	Long accountId;
	
	String cseToken;
	String cardSuffix;
	
	String cardNumber;
	String cardHolder;
	String expiryMonth;
	String expiryYear;
	String cvc;
	
	Date dateCreated;

	@Override
	public String hashKey() {
		return CardTableSpec.ACCOUNT_ID;
	}
	
}
