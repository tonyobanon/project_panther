package com.re.paas.internal.billing;

import java.util.Date;

public class CseTokenInfo  extends BaseCardInfo{

	private String cseToken;
	private String cardSuffix;

	public Long getAccountId() {
		return accountId;
	}

	public CseTokenInfo setAccountId(Long id) {
		this.accountId = id;
		return this;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public CseTokenInfo setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		return this;
	}
	
	public String getCseToken() {
		return cseToken;
	}

	public CseTokenInfo setCseToken(String cseToken) {
		this.cseToken = cseToken;
		return this;
	}

	public String getCardSuffix() {
		return cardSuffix;
	}

	public CseTokenInfo setCardSuffix(String cardSuffix) {
		this.cardSuffix = cardSuffix;
		return this;
	}

}
