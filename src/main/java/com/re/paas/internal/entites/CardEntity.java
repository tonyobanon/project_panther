package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class CardEntity {

	@Id
	Long accountId;
	
	String cseToken;
	String cardSuffix;
	
	String cardNumber;
	String cardHolder;
	String expiryMonth;
	String expiryYear;
	String cvc;
	
	Date dateCreated;

	

	public Long getAccountId() {
		return accountId;
	}

	public CardEntity setAccountId(Long accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getCseToken() {
		return cseToken;
	}

	public CardEntity setCseToken(String cseToken) {
		this.cseToken = cseToken;
		return this;
	}

	public String getCardSuffix() {
		return cardSuffix;
	}

	public CardEntity setCardSuffix(String cardSuffix) {
		this.cardSuffix = cardSuffix;
		return this;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public CardEntity setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
		return this;
	}

	public String getCardHolder() {
		return cardHolder;
	}

	public CardEntity setCardHolder(String cardHolder) {
		this.cardHolder = cardHolder;
		return this;
	}

	public String getExpiryMonth() {
		return expiryMonth;
	}

	public CardEntity setExpiryMonth(String expiryMonth) {
		this.expiryMonth = expiryMonth;
		return this;
	}

	public String getExpiryYear() {
		return expiryYear;
	}

	public CardEntity setExpiryYear(String expiryYear) {
		this.expiryYear = expiryYear;
		return this;
	}

	public String getCvc() {
		return cvc;
	}

	public CardEntity setCvc(String cvc) {
		this.cvc = cvc;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CardEntity setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
}
