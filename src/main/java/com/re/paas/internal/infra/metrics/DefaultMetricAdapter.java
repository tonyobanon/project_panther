package com.re.paas.internal.infra.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.forms.Form;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.infra.metrics.Metrics;
import com.re.paas.api.infra.metrics.MetricsAdapter;
import com.timgroup.statsd.NonBlockingStatsDClient;

public class DefaultMetricAdapter implements MetricsAdapter {

	private static final String defaultBucket = "default";
	private static Map<String, Metrics> clients = Collections.synchronizedMap(new HashMap<>());

	@Override
	public String name() {
		return "default";
	}

	@Override
	public String title() {
		return "Default";
	}

	@Override
	public String iconUrl() {
		return "https://avatars2.githubusercontent.com/u/8270030?s=400&v=4";
	}

	@Override
	public Form initForm() {

		Section section = new Section();
		section.setTitle("statsd")

				.withField(new SimpleField("statsd_server_url", InputType.TEXT, ClientRBRef.get("server_url")));

		return new Form().addSection(section);
	}

	@Override
	public Metrics getResource(Map<String, String> fields, String bucket) {

		if (bucket == null) {
			bucket = defaultBucket;
		}

		Metrics client = clients.get(bucket);

		if (client != null) {
			return client;
		}

		String serverUrl = fields.get("statsd_server_url");

		client = new MetricsClient(bucket, serverUrl, new NonBlockingStatsDClient(bucket, serverUrl, 8125));

		clients.put(bucket, client);
		
		return client;
	}
	
	@Override
	public void close() {
		clients.values().forEach(m -> m.stop());
		clients.clear();
	}
}
