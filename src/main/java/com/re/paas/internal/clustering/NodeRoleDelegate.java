package com.re.paas.internal.clustering;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.AbstractNodeRoleDelegate;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.infra.cloud.CloudEnvironment;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.compute.Scheduler;

@BlockerTodo("Add logic for SpiDelegate.remove(..), store nodeRoles as SPI Resource")

@DelegateSpec(dependencies = { SpiType.CLOUD_ENVIRONMENT, SpiType.EVENT, SpiType.FUNCTION })
public class NodeRoleDelegate extends AbstractNodeRoleDelegate {

	private static AbstractMasterNodeRole masterNodeRole;

	private static Map<String, NodeRole> nodeRoles = Collections.synchronizedMap(new LinkedHashMap<String, NodeRole>());
	private static Map<String, NodeRole> allRoles = Collections.synchronizedMap(new HashMap<String, NodeRole>());

	private static final Integer nodeRoleStartTimeout = 10000;

	@Override
	public DelegateInitResult init() {

		NodeRegistry.get().start().join();

		Map<String, NodeRole> roles = new HashMap<>();

		Consumer<Class<NodeRole>> consumer = c -> {

			if (Modifier.isAbstract(c.getModifiers())) {
				return;
			}

			NodeRole role = ClassUtils.createInstance(c);

			allRoles.put(role.name(), role);

			roles.put(role.name(), role);
		};

		forEach(consumer);

		processRoles(roles);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	public Boolean applies() {
		return CloudEnvironment.get().isClustered();
	}

	@Override
	protected void add(List<Class<NodeRole>> classes) {

		Map<String, NodeRole> roles = new HashMap<>();

		classes.forEach(c -> {
			NodeRole o = ClassUtils.createInstance(c);
			allRoles.put(o.name(), o);

			roles.put(o.name(), o);
		});

		processRoles(roles);
	}

	protected void processRoles(Map<String, NodeRole> newRoles) {

		List<NodeRole> roles = new ArrayList<NodeRole>();

		newRoles.forEach((k, v) -> {
			processRoles(roles, null, v);
		});

		// Start roles sequentially

		int succeeded = 0;
		int index = 0;

		AbstractEventDelegate eventDelegate = AbstractEventDelegate.getInstance();

		while (index < roles.size()) {

			ObjectWrapper<RoleStartCompleteEvent> result = new ObjectWrapper<>();

			Thread current = Thread.currentThread();

			eventDelegate.one(RoleStartCompleteEvent.class, (evt) -> {
				result.set(evt);

				current.interrupt();
			});

			NodeRole role = roles.get(index);

			Scheduler.now(() -> {
				role.start();
			});

			try {

				Thread.sleep(nodeRoleStartTimeout);

			} catch (InterruptedException e) {

				if (result.get() == null) {
					Logger.get().error("Role: " + role.name() + " timeout while attempting to start");
				}

				if (result.get().isSucceeded()) {
					succeeded++;

					if (masterNodeRole == null && AbstractMasterNodeRole.class.isAssignableFrom(role.getClass())) {
						masterNodeRole = (AbstractMasterNodeRole) role;
					}

				} else {
					Logger.get().error(
							"Role: " + role.name() + " failed to start with error: " + result.get().getMessage());
				}
			}

			index++;
			continue;
		}

		Logger.get().info("Successfully started " + succeeded + "/" + roles.size() + " role(s)");
	}

	private static boolean processRoles(List<NodeRole> accumulator, String dependants, NodeRole role) {

		for (Class<? extends NodeRole> c : role.dependencies()) {

			NodeRole r = ClassUtils.createInstance(c);

			if (dependants != null) {
				if (dependants.contains(r.name())) {
					// Circular reference was detected
					Exceptions.throwRuntime(new RuntimeException("Circular reference was detected: "
							+ (dependants + " -> " + role.name() + " -> " + r.name()).replaceAll(r.name(),
									"(" + r.name() + ")")));
				}

				if (!processRoles(accumulator, dependants + " -> " + role.name(), r)) {
					return false;
				}

			} else {
				if (!processRoles(accumulator, role.name(), r)) {
					return false;
				}
			}
		}

		boolean applies = role.applies();

		if (!nodeRoles.containsKey(role.name()) && applies) {

			Logger.get().info("Registering role: " + role.name());

			accumulator.add(role);
			nodeRoles.put(role.name(), role);
		}

		return applies;
	}

	@Override
	public Map<String, NodeRole> getAllRoles() {
		return allRoles;
	}

	public Map<String, NodeRole> getNodeRoles() {
		return nodeRoles;
	}

	@Override
	public void shutdown() {
		nodeRoles.values().forEach(role -> {
			role.stop();
		});
	}

	@Override
	public AbstractMasterNodeRole getMasterRole() {
		return masterNodeRole;
	}
}