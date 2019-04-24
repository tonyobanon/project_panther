package com.re.paas.internal.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.infra.cloud.AbstractCloudEnvironmentDelegate;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.utils.ClassUtils;

public class CloudEnvironmentDelegate extends AbstractCloudEnvironmentDelegate {


	private static CloudEnvironment instance;

	@Override
	public DelegateInitResult init() {
		
		forEach(c -> {

			CloudEnvironment e = ClassUtils.createInstance(c);

			if (get(e.id()) != null) {
				throw new RuntimeException("Duplicate CloudEnvironment definitions exists with id: " + e.id());
			}
			set(e.id(), e);
		});
		return DelegateInitResult.SUCCESS;
	}

	@Override
	public CloudEnvironment getInstance() {

		if (instance != null) {
			return instance;
		}

		List<CloudEnvironment> instances = getInstances();

		for (CloudEnvironment e : instances) {
			if (e.applies()) {
				instance = e;
				return instance;
			}
		}

		return null;
	}


	@Override
	public List<CloudEnvironment> getInstances() {

		Map<Object, Object> m = getAll();
		List<CloudEnvironment> r = new ArrayList<CloudEnvironment>();

		m.forEach((k, v) -> {
			CloudEnvironment env = (CloudEnvironment) v;
			if (env.enabled()) {
				r.add(env);
			}
		});
		return r;
	}
	
}
