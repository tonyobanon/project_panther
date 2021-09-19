package com.re.paas.internal.clustering;

import static java.util.regex.Pattern.quote;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.ConfigurationChildBuilder;
import org.infinispan.configuration.cache.StorageType;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.multimap.api.embedded.EmbeddedMultimapCacheManagerFactory;
import org.infinispan.multimap.api.embedded.MultimapCacheManager;

import com.google.common.base.Splitter;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Prototype;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.clustering.ClusteringServices;
import com.re.paas.api.clustering.Member;
import com.re.paas.api.clustering.SelectionMetric;
import com.re.paas.api.clustering.protocol.Server;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.logging.LoggerFactory;
import com.re.paas.api.tasks.Affinity;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.infra.cache.infinispan.InfinispanMashaller;

@Prototype
public class ClusteringServicesImpl implements ClusteringServices {

	private static Logger LOG = LoggerFactory.get().getLog(ClusteringServicesImpl.class);

	private static DefaultCacheManager cacheManager;
	private static Server server;

	private static List<String> roles = new ArrayList<>();

	public static ConfigurationChildBuilder getDefaultCacheConfiguration() {

		// Make the default cache a distributed synchronous one
		ConfigurationBuilder builder = new ConfigurationBuilder();

		return builder.clustering().cacheMode(CacheMode.SCATTERED_SYNC).memory().storage(StorageType.OFF_HEAP)
				.statistics().enable();
	}

	@Override
	public void addRole(String role) {
		roles.add(role);
	}

	@Override
	public CompletableFuture<Void> start() throws IOException {
		return CompletableFuture.runAsync(() -> {
			try {
				start0();
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		});
	}

	@BlockerTodo("Make the ConfigurationBuilder setings configurable individually in DistributedStoreConfig")
	private void start0() throws IOException {

		// Setup up a clustered cache manager
		GlobalConfigurationBuilder global = GlobalConfigurationBuilder.defaultClusteredBuilder();

		// Add marshaller
		global.serialization().marshaller(new InfinispanMashaller());

		// global.defaultCacheName(defaultCacheName).cacheContainer();

		// Initialize the cache manager
		cacheManager = new DefaultCacheManager(global.build(), true);
	
		
		
		cacheManager.executor().execute((Runnable & Serializable) () -> {
			
		});
		
		// Start cluster server
		server = Server.get(
				new InetSocketAddress(getJGroupsMemberAddresses().get(0).getAddress(), Utils.randomInt(1024, 65535)));

		server.start().join();
		
		assert server.isOpen();
		
		LOG.info(
				"Joined cluster, isMaster=%s, memberId=%s, address=%s:%s",
				isMaster(), getMemberId(), server.host(), server.port());

		
//		// Register existing members in ClientFactory
//		ClientFactory cFactory = ClientFactory.get();
//		getMembers().keySet().stream().forEach(cFactory::addMember);

	}

	public MultimapCacheManager<String, Object> getMultimapCacheManager() {

		// create or obtain a MultimapCacheManager passing the EmbeddedCacheManager

		@SuppressWarnings("unchecked")
		MultimapCacheManager<String, Object> multimapCacheManager = (MultimapCacheManager<String, Object>) EmbeddedMultimapCacheManagerFactory
				.from(cacheManager);

		return multimapCacheManager;
	}

	public DefaultCacheManager getCacheManager() {
		return getCacheManager0();
	}

	static DefaultCacheManager getCacheManager0() {
		return cacheManager;
	}

	public Boolean isMaster() {
		return getCacheManager0().getClusterSize() == 1;
	}

	@Override
	public Server getServer() {
		return server;
	}

	public Short getMemberId() {
		return getJGroupsId(cacheManager.getNodeAddress());
	}

	public Member getMember(Short memberId) {
		return getMembers().get(memberId);
	}

	public Map<Short, Member> getMembers() {

		List<String> memberNames = getJGroupsMemberNames();
		List<InetSocketAddress> memberAddresses = getJGroupsMemberAddresses();

		Map<Short, Member> result = new HashMap<>(memberNames.size());

		for (int i = 0; i < memberNames.size(); i++) {

			String name = memberNames.get(i);
			Short id = getJGroupsId(name);

			InetSocketAddress addr = new InetSocketAddress(memberAddresses.get(i).getAddress(), id);

			result.put(id, new Member(id, name, addr));
		}

		return result;
	}

	private static Short getJGroupsId(String name) {
		String arr[] = name.split("-");
		return Short.parseShort(arr[arr.length - 1]);
	}

	static List<String> getJGroupsMemberNames() {
		return toList(getCacheManager0().getClusterMembers());
	}

	static List<InetSocketAddress> getJGroupsMemberAddresses() {
		return toList(getCacheManager0().getClusterMembersPhysicalAddresses()).stream().map(s -> {
			String[] arr = s.split(":");
			return new InetSocketAddress(arr[0], Integer.parseInt(arr[1]));
		}).collect(Collectors.toList());
	}

	private static List<String> toList(String trait) {
		return Splitter.on(Pattern.quote(", ")).splitToList(trait.replaceAll(quote("[") + "|" + quote("]"), ""));
	}

	@BlockerTodo
	@Override
	public Collection<Short> getAvailableMember(SelectionMetric metric, int maxCount) {
		return Set.of(getMemberId());
	}

	@BlockerTodo
	@Override
	public Collection<Short> getAvailableMember(Affinity affinity, int maxCount) {
		return Set.of(getMemberId());
	}

	@Override
	public void addClusterWideTask(String name, ClusterWideTask task) {
		// Todo
	}

//	private static Runnable getRunnable(ClusterWideTask task) {
//		return (Runnable & Serializable) (() -> {
//
//			if (task.getPredicate().getAsBoolean()) {
//				task.getTask().run();
//			}
//		});
//	}

}
