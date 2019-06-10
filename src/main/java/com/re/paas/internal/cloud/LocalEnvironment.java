package com.re.paas.internal.cloud;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.clustering.classes.InstanceProfile;
import com.re.paas.api.infra.cloud.AbstractProviderHandler;
import com.re.paas.api.infra.cloud.InstanceCredential;
import com.re.paas.api.infra.cloud.Tags;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.networking.NIC;

@BlockerTodo
public class LocalEnvironment implements CloudEnvironmentAdapter {

	// The same will be used for clustering and http
	private InetAddress address;

	public LocalEnvironment() {
		for (InetAddress address : NIC.getAllAddress()) {
			if (address.isLoopbackAddress()) {
				this.address = address;
				break;
			}
		}
	}

	@Override
	public String id() {
		return "local";
	}

	@Override
	public Boolean enabled() {
		return true;
	}

	@Override
	public Boolean applies() {
		return this.address != null;
	}

	@Override
	public Boolean isProduction() {
		return false;
	}

	@Override
	public String title() {
		return "Local";
	}

	@Override
	public InetAddress httpHost() {
		return this.address;
	}

	@Override
	public InetAddress clusteringHost() {
		return this.address;
	}

	@Override
	public Boolean isClustered() {
		return false;
	}

	@Override
	public Map<String, String> getInstanceTags() {
		Map<String, String> tags = new HashMap<>();

		tags.put(Tags.RESOURCE_NAME_TAG, "local-node");
		tags.put(Tags.CLUSTER_NAME_TAG, "local-cluster");
		tags.put(Tags.HTTP_PORT_TAG, "80");
		tags.put(Tags.HTTPS_PORT_TAG, "443");
		tags.put(Tags.CLUSTERING_PORT_TAG, "5000");
		tags.put(Tags.WKA_HOST_TAG, this.address.getHostAddress());
		tags.put(Tags.CLUSTER_ACCESS_KEY_TAG, Utils.newRandom());
		tags.put(Tags.CLUSTER_SECRET_KEY_TAG, Utils.newRandom());
		tags.put(Tags.MASTER_TAG, "true");

		return tags;
	}

	@Override
	public AbstractProviderHandler providerDelegate() {
		return new AbstractProviderHandler() {

			@Override
			public void stopVM(String instanceId) {
			}

			@Override
			public InstanceCredential startVM(Boolean master, Map<String, String> tags) {
				return null;
			}

			@Override
			public InstanceProfile getInstanceProfile() {
				return null;
			}

			@Override
			public String getInstanceId() {
				return Utils.newRandom();
			}
		};
	}

}
