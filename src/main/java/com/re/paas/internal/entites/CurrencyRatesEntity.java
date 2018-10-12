package com.re.paas.internal.entites;

import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class CurrencyRatesEntity {

	@Id
	String pair;
	Double rate;
	Date lastUpdated;

	public String getPair() {
		return pair;
	}

	public CurrencyRatesEntity setPair(String pair) {
		this.pair = pair;
		return this;
	}

	public Double getRate() {
		return rate;
	}

	public CurrencyRatesEntity setRate(Double rate) {
		this.rate = rate;
		return this;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public CurrencyRatesEntity setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
		return this;
	}

}
