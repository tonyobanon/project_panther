package com.re.paas.internal.clustering;

import static com.re.paas.api.clustering.generic.GenericFunction.DISPATCH_EVENT;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.multimap.api.embedded.EmbeddedMultimapCacheManagerFactory;
import org.infinispan.multimap.api.embedded.MultimapCache;
import org.infinispan.multimap.api.embedded.MultimapCacheManager;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Prototype;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.Function;
import com.re.paas.api.clustering.Member;
import com.re.paas.api.clustering.SelectionMetric;
import com.re.paas.api.clustering.classes.ClusterDestination;
import com.re.paas.api.clustering.events.MemberJoinEvent;
import com.re.paas.api.clustering.events.MemberLeaveEvent;
import com.re.paas.api.clustering.master.MasterFunction;
import com.re.paas.api.clustering.protocol.Client;
import com.re.paas.api.clustering.protocol.ClientFactory;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.networking.InetAddressResolver;
import com.re.paas.api.runtime.ParameterizedExecutable;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.AppDelegate;
import com.re.paas.internal.clustering.model.MemberSetupRequest;
import com.re.paas.internal.clustering.model.MemberSetupResult;
import com.re.paas.internal.compute.Scheduler;

@Prototype
public class ClusteringServicesImpl implements ClusteringServices {

	private static Logger LOG = LoggerFactory.get().getLog(ClusteringServicesImpl.class);

	private static final String genericMultiCache = "generic-multi-cache";
	private static final String defaultCacheName = "default-cache";

	private static final String clusterMembersCache = "clusterMembers";
	private static final String unassignedMemberIds = "unassignedMemberIds";

	private static DefaultCacheManager cacheManager;
	private static Server server;

	static Short memberId;
	private static final Map<String, ParameterizedExecutable<?, ?>> masterOnboardingTasks = new HashMap<>();

	@BlockerTodo("Make the ConfigurationBuilder setings configurable individually in DistributedStoreConfig")
	public void start() {

		// Setup up a clustered cache manager
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();

		// Add marshaller
		global.serialization().marshaller(new InfinispanMashaller());
		global.defaultCacheName(defaultCacheName);

		// Make the default cache a distributed synchronous one
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.clustering().cacheMode(CacheMode.SCATTERED_SYNC).memory().storageType(StorageType.OFF_HEAP)
				.jmxStatistics().enable();

		LOG.info("Starting infinispan cache");

		// Initialize the cache manager
		cacheManager = new DefaultCacheManager(global.build(), builder.build());
		// cacheManager.start();

		// Create member instance
		Short memberId = getNextMemberId();
		InetSocketAddress host = new InetSocketAddress(InetAddressResolver.get().getInetAddress(),
				Utils.randomInt(1024, 65535));

		Member member = new Member(memberId, host);

		Map<Short, Member> members = getClusterMembersCache();

		LOG.info("Add current member to cache");

		// Add current member to list
		members.put(member.getMemberId(), member);

		LOG.info("Starting server");

		// Start cluster server
		server = Server.get(member.getHost());
		server.start().join();

		if (!isMaster()) {

			// Register existing members in ClientFactory
			ClientFactory cFactory = ClientFactory.get();
			members.keySet().stream().forEach(cFactory::addMember);

			// Request master to setup this member
			MemberSetupResult r = Client.get(getMaster().getMemberId())
					.execute(MasterFunction.MEMBER_SETUP, new MemberSetupRequest(), MemberSetupResult.class).join();

			if (!r.isSuccess()) {
				Exceptions.throwRuntime(r.getError());
			}

			// Dispatch MemberJoinEvent to all members
			Function.executeWait(ClusterDestination.ALL_NODES, DISPATCH_EVENT,
					new MemberJoinEvent(member.getMemberId()));

		} else {

			// Start metrics aggregator
			MetricsAggregator.start();
		}

		// Start metrics collector
		MetricsCollector.start();

		// add shutdown hook
		AppDelegate.addFinalizer(() -> {

			if (!isMaster()) {

				// Register current member Id as unused
				addUnassignedMemberId(member.getMemberId());

				// Remove member from members cache
				getClusterMembersCache().remove(member.getMemberId());

				// Dispatch MemberLeaveEvent to all members
				Function.execute(ClusterDestination.ALL_NODES, DISPATCH_EVENT,
						new MemberLeaveEvent(member.getMemberId()));

			} else {

				// The old master appoints a new master as dispatches MasterAppointmentEvent to
				// it

				// The new master accepts the request, it performs any necessary onboarding task
				// including register recurringMasterTasks
				// It takes on the memberId of 0

				// The new master informs everyone of a new master by emitting MasterChangeEvent
			}

			// Stop cluster server
			if (server.isOpen()) {
				server.stop().join();
			}

			// Stop the cache manager and release all resources
			cacheManager.stop();

		});
	}

	private Map<Short, Member> getClusterMembersCache() {
		return getCacheManager().getCache(clusterMembersCache);
	}

	/**
	 * This is a generic multimap cache, used internally by
	 * {@link ClusteringServicesImpl}
	 * 
	 * @return
	 */
	private MultimapCache<String, Object> getGenericMultimapCache() {
		return getMultimapCacheManager().get(genericMultiCache);
	}

	private List<Short> getUnassignedMemberIds() {

		CompletableFuture<List<Short>> c = getGenericMultimapCache().get(unassignedMemberIds)
				.thenApply(ids -> ids.stream().map(id -> (Short) id).collect(Collectors.toUnmodifiableList()));

		return c.join();
	}

	private Boolean removeUnassignedMemberId(Short memberId) {
		return getGenericMultimapCache().remove(unassignedMemberIds, memberId).join();
	}

	private void addUnassignedMemberId(Short memberId) {
		getGenericMultimapCache().put(unassignedMemberIds, memberId);
	}

	public MultimapCacheManager<String, Object> getMultimapCacheManager() {

		// create or obtain a MultimapCacheManager passing the EmbeddedCacheManager

		@SuppressWarnings("unchecked")
		MultimapCacheManager<String, Object> multimapCacheManager = (MultimapCacheManager<String, Object>) EmbeddedMultimapCacheManagerFactory
				.from(cacheManager);

		return multimapCacheManager;
	}

	public DefaultCacheManager getCacheManager() {
		return cacheManager;
	}

	public Boolean isMaster() {
		return memberId == 0;
	}

	@Override
	public Server getServer() {
		return server;
	}

	public Member getMember() {
		return getMember(memberId);
	}

	public Member getMaster() {
		return getMember((short) 0);
	}

	public Member getMember(Short memberId) {
		return getClusterMembersCache().get(memberId);
	}

	public Map<Short, Member> getMembers() {
		return Collections.unmodifiableMap(getClusterMembersCache());
	}

	private Short getNextMemberId() {

		List<Short> ids = getUnassignedMemberIds();

		if (!ids.isEmpty()) {

			Short id = ids.get(0);
			removeUnassignedMemberId(id);

			return id;
		}

		return (short) getClusterMembersCache().size();
	}

	@BlockerTodo
	public Short getAvailableMember(SelectionMetric metric) {
		return 0;
	}

	/**
	 * Note: MasterInitTasks are not stored in the cluster's data grid, but instead
	 * stored locally on the master, and manually transferred to newly appointed
	 * master(s)
	 * 
	 * @param name
	 * @param task
	 * @param predicate
	 * @param initialExecutionDelay In Seconds
	 * 
	 */
	@Override
	public void addMasterOnboardingTask(String name, ParameterizedExecutable<Object, Object> task,
			Predicate<?> predicate, Long initialExecutionDelay) {

		if (isMaster() && !masterOnboardingTasks.containsKey(name)) {

			// register task
			masterOnboardingTasks.put(name, task);

			Runnable r = () -> {

				// execute task
				Object response = task.getFunction().apply(task.getParameter());

				LOG.info("Executed master onboarding task: " + name + " and got response: " + response);
			};

			if (initialExecutionDelay == null) {
				r.run();
			} else {
				Scheduler.schedule(r, initialExecutionDelay, TimeUnit.SECONDS);
			}
		}
	}

}
