package com.re.paas.api.clustering;

import java.util.Map;
import java.util.function.Predicate;

import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.runtime.SecureMethod;
import com.re.paas.internal.clustering.MetricsAggregator;

public interface ClusteringServices {

	public static ClusteringServices get() {
		return Singleton.get(ClusteringServices.class);
	}

	@SecureMethod
	void start();

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
	Short getAvailableMember(SelectionMetric metric);

	void addMasterOnboardingTask(String name, ParameterizedExecutable<Object, Object> task, Predicate<?> predicate,
			Long initialExecutionDelay);

}
