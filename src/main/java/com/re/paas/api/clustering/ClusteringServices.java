package com.re.paas.api.clustering;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.re.paas.api.Singleton;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.internal.clustering.MasterOnboardingTask;
import com.re.paas.internal.clustering.MetricsAggregator;

public interface ClusteringServices {

	public static ClusteringServices get() {
		return Singleton.get(ClusteringServices.class);
	}

	@SecureMethod
	void start() throws IOException;
	
	Boolean isStarted();

	@SecureMethod
	Object getMultimapCacheManager();

	@SecureMethod
	Object getCacheManager();

	Boolean isMaster();

	Member getMember();
	
	Server getServer();

	Member getMaster();

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
	void addMasterOnboardingTask(String name, MasterOnboardingTask task);

}
