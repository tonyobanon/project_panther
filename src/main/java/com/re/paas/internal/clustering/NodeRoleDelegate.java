package com.re.paas.internal.clustering;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.clustering.AbstractClusterFunction;
import com.re.paas.api.clustering.AbstractMasterNodeRole;
import com.re.paas.api.clustering.AbstractNodeRoleDelegate;
import com.re.paas.api.clustering.NodeRegistry;
import com.re.paas.api.clustering.NodeRole;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.DelegateInitResult;
import com.re.paas.api.spi.DelegateSpec;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.internal.compute.Scheduler;

@BlockerTodo("Add logic for SpiDelegate.remove(..), store nodeRoles as SPI Resource")

@DelegateSpec(dependencies = {SpiTypes.CLOUD_ENVIRONMENT})
public class NodeRoleDelegate extends AbstractNodeRoleDelegate {

	private static AbstractMasterNodeRole masterNodeRole;
	
	private static Map<String, NodeRole> nodeRoles = Collections.synchronizedMap(new LinkedHashMap<String, NodeRole>());
	private static Map<String, NodeRole> allRoles = Collections.synchronizedMap(new HashMap<String, NodeRole>());

	@Override
	public DelegateInitResult init() {
		
		NodeRegistry.get().start().join();

		Map<String, NodeRole> roles = new HashMap<>();

		Consumer<Class<NodeRole>> consumer = c -> {
			NodeRole o = ClassUtils.createInstance(c);
			allRoles.put(o.name(), o);

			roles.put(o.name(), o);
		};

		forEach(consumer);

		processRoles(roles);
		return DelegateInitResult.SUCCESS;
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

		AbstractClusterFunction.getDelegate().scanFunctions();

		// Start roles sequentially

		int succeeded = 0;
		int index = 0;

		Thread currentThread = Thread.currentThread();

		AbstractEventDelegate eventDelegate = AbstractEventDelegate.getInstance();
		
		while (index < roles.size()) {

			ObjectWrapper<RoleStartCompleteEvent> result = new ObjectWrapper<>();

			eventDelegate.one(RoleStartCompleteEvent.class, (evt) -> {
				result.set(evt);
				currentThread.interrupt();
			});

			NodeRole role = roles.get(index);
			Logger.get().info("Starting role: " + role.name());

			Scheduler.now(() -> {
				role.start();
			});

			try {
				currentThread.wait();

			} catch (InterruptedException e) {

				if (result.get().isSucceeded()) {
					succeeded++;
					
					if(masterNodeRole == null && AbstractMasterNodeRole.class.isAssignableFrom(role.getClass())) {
						masterNodeRole = (AbstractMasterNodeRole) role;
					}
					
				} else {
					Logger.get().error(
							"Role: " + role.name() + " failed to start with error: " + result.get().getMessage());
				}

				index++;
				continue;
			}
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
	public void destroy() {
		nodeRoles.values().forEach(role -> {
			role.stop();
		});
	}

	@Override
	public AbstractMasterNodeRole getMasterRole() {
		return masterNodeRole;
	}
}