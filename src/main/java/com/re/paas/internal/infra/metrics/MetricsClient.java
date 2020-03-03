package com.re.paas.internal.infra.metrics;

import com.re.paas.api.infra.metrics.Metrics;
import com.timgroup.statsd.StatsDClient;

public class MetricsClient implements Metrics {
	
	private final String prefix;
	private final String host;
	private final StatsDClient client;
	
	
	MetricsClient(String prefix, String host, StatsDClient client) {
		this.prefix = prefix;
		this.host = host;
		this.client = client;
	}
	
	@Override
	public String bucket() {
		return this.prefix;
	}
	
	@Override
	public String host() {
		return this.host;
	}
	
	@Override
	public Metrics bucket(String bucket) {
		return Metrics.get(bucket);
	}

	@Override
	public void stop() {
		this.client.stop();
	}

	@Override
	public void count(String aspect, long delta) {
		this.client.count(aspect, delta);
	}

	@Override
	public void count(String aspect, long delta, double sampleRate) {
		this.client.count(aspect, delta, sampleRate);
	}

	@Override
	public void incrementCounter(String aspect) {
		this.client.incrementCounter(aspect);
	}

	@Override
	public void increment(String aspect) {
		this.client.increment(aspect);
	}

	@Override
	public void decrementCounter(String aspect) {
		this.client.decrementCounter(aspect);
	}

	@Override
	public void decrement(String aspect) {
		this.client.decrement(aspect);
	}

	@Override
	public void recordGaugeValue(String aspect, long value) {
		this.client.recordGaugeValue(aspect, value);
	}

	@Override
	public void recordGaugeValue(String aspect, double value) {
		this.client.recordGaugeValue(aspect, value);
	}

	@Override
	public void recordGaugeDelta(String aspect, long delta) {
		this.client.recordGaugeDelta(aspect, delta);
	}

	@Override
	public void recordGaugeDelta(String aspect, double delta) {
		this.client.recordGaugeDelta(aspect, delta);
	}

	@Override
	public void gauge(String aspect, long value) {
		this.client.gauge(aspect, value);
	}

	@Override
	public void gauge(String aspect, double value) {
		this.client.gauge(aspect, value);
	}

	@Override
	public void recordSetEvent(String aspect, String eventName) {
		this.client.recordSetEvent(aspect, eventName);
	}

	@Override
	public void set(String aspect, String eventName) {
		this.client.set(aspect, eventName);
	}

	@Override
	public void recordExecutionTime(String aspect, long timeInMs) {
		this.client.recordExecutionTime(aspect, timeInMs);
	}

	@Override
	public void recordExecutionTime(String aspect, long timeInMs, double sampleRate) {
		this.client.recordExecutionTime(aspect, timeInMs, sampleRate);
	}

	@Override
	public void recordExecutionTimeToNow(String aspect, long systemTimeMillisAtStart) {
		this.client.recordExecutionTimeToNow(aspect, systemTimeMillisAtStart);
	}

	@Override
	public void time(String aspect, long value) {
		this.client.time(aspect, value);
	}

}
