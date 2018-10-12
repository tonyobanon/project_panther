package com.re.paas.internal.billing;

import java.math.BigDecimal;

public class PaymentRequest {

	private String reference;
	private BigDecimal amount;
	private String currency;
	
	private String customerEmail;
	private Long customerId;
	private String customerPhone;
	
	private BaseCardInfo cardInfo;
	private BillingAddress billingAddress;

	public String getReference() {
		return reference;
	}

	public PaymentRequest setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public PaymentRequest setAmount(BigDecimal amount) {
		this.amount = amount;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public PaymentRequest setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public PaymentRequest setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
		return this;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public PaymentRequest setCustomerId(Long customerId) {
		this.customerId = customerId;
		return this;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public PaymentRequest setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
		return this;
	}

	public BaseCardInfo getCardInfo() {
		return cardInfo;
	}

	public PaymentRequest setCardInfo(BaseCardInfo cardInfo) {
		this.cardInfo = cardInfo;
		return this;
	}

	public BillingAddress getBillingAddress() {
		return billingAddress;
	}

	public PaymentRequest setBillingAddress(BillingAddress billingAddress) {
		this.billingAddress = billingAddress;
		return this;
	}

}
