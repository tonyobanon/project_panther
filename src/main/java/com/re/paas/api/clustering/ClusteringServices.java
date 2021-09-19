package com.re.paas.api.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.re.paas.api.Singleton;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.internal.clustering.ClusterWideTask;

public interface ClusteringServices {

	public static ClusteringServices get() {
		return Singleton.get(ClusteringServices.class);
	}

	@SecureMethod
	CompletableFuture<Void> start() throws IOException;

	@SecureMethod
	Object getMultimapCacheManager();

	@SecureMethod
	Object getCacheManager();
	
	void addRole(String role);

	Boolean isMaster();

	Short getMemberId();
	
	Server getServer();

	Member getMember(Short memberId);

	Map<Short, Member> getMembers();

	/**
	 * This should return a member that best matches the provided metric
	 * 
	 * A call needs to be made to the master, to retrieve the info. See here more
	 * more context {@link MetricsAggregator}
	 * 
	 * There should a pre-determined duration for which the member must have been in
	 * the cluster. This will ensure that the member must have been properly
	 * initialized
	 * 
	 * 
	 * @return
	 */
	Collection<Short> getAvailableMember(SelectionMetric metric, int maxCount);
	
	Collection<Short> getAvailableMember(Affinity affinity, int maxCount);

	@SecureMethod
	void addClusterWideTask(String name, ClusterWideTask task);
	
	

}
