package com.re.paas.internal.billing;

import java.util.Date;

public class CardInfo extends BaseCardInfo{

	private String cardNumber;
	private String cardHolder;
	private String expiryMonth;
	private String expiryYear;
	private String cvc;


	public Long getAccountId() {
		return accountId;
	}

	public CardInfo setAccountId(Long id) {
		this.accountId = id;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CardInfo setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
	public String getCardNumber() {
		return cardNumber;
	}

	public CardInfo setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public CardInfo setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
		return this;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public CardInfo setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
		return this;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public CardInfo setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
		return this;
	}

	public String getCvc() {
		return cvc;
	}

	public CardInfo setCvc(String cvc) {
		this.cvc = cvc;
		return this;
	}
}
