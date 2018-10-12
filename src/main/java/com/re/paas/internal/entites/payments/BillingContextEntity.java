package com.re.paas.internal.entites.payments;

import java.math.BigDecimal;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class BillingContextEntity {

	@Id
	Long accountId;

	//@Index
	String invoiceId;

	Integer status;

	String currency;

	BigDecimal totalDue;

	Date dateUpdated;

	public Long getAccountId() {
		return accountId;
	}

	public BillingContextEntity setAccountId(Long accountId) {
		this.accountId = accountId;
		return this;
	}

	public Long getInvoiceId() {
		return Long.parseLong(invoiceId);
	}

	public BillingContextEntity setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId.toString();
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public BillingContextEntity setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public String getCurrency() {
		return currency;
	}

	public BillingContextEntity setCurrency(String currency) {
		this.currency = currency;
		return this;
	}

	public BigDecimal getTotalDue() {
		return totalDue;
	}

	public BillingContextEntity setTotalDue(BigDecimal totalDue) {
		this.totalDue = totalDue;
		return this;
	}

	public BillingContextEntity incrementTotalDue(BigDecimal amount) {
		this.totalDue = totalDue.add(amount);
		return this;
	}

	public BillingContextEntity decrementTotalDue(BigDecimal amount) {
		this.totalDue = totalDue.subtract(amount);
		return this;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public BillingContextEntity setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
		return this;
	}

}
