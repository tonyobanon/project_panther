package com.re.paas.internal.roles;

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
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.roles.AbstractMasterRole;
import com.re.paas.api.roles.AbstractNodeRoleDelegate;
import com.re.paas.api.roles.AbstractRole;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.compute.Scheduler;

@BlockerTodo("Add logic for SpiDelegate.remove(..), store nodeRoles as SPI Resource")

@DelegateSpec(dependencies = { SpiType.CLOUD_ENVIRONMENT, SpiType.EVENT, SpiType.FUNCTION })
public class NodeRoleDelegate extends AbstractNodeRoleDelegate {

	private static AbstractMasterRole masterNodeRole;

	private static Map<String, AbstractRole> nodeRoles = Collections.synchronizedMap(new LinkedHashMap<String, AbstractRole>());
	private static Map<String, AbstractRole> allRoles = Collections.synchronizedMap(new HashMap<String, AbstractRole>());

	private static final Integer nodeRoleStartTimeout = 10000;

	@Override
	public DelegateInitResult init() {

		Map<String, AbstractRole> roles = new HashMap<>();

		Consumer<Class<AbstractRole>> consumer = c -> {

			if (Modifier.isAbstract(c.getModifiers())) {
				return;
			}

			AbstractRole role = com.re.paas.internal.classes.ClassUtil.createInstance(c);

			allRoles.put(role.name(), role);

			roles.put(role.name(), role);
		};

		DelegateInitResult r = forEach(consumer);

		processRoles(roles);
		
		return r;
	}

	@Override
	protected void add(List<Class<AbstractRole>> classes) {

		Map<String, AbstractRole> roles = new HashMap<>();

		classes.forEach(c -> {
			AbstractRole o = com.re.paas.internal.classes.ClassUtil.createInstance(c);
			allRoles.put(o.name(), o);

			roles.put(o.name(), o);
		});

		processRoles(roles);
	}

	protected void processRoles(Map<String, AbstractRole> newRoles) {

		List<AbstractRole> roles = new ArrayList<AbstractRole>();

		newRoles.forEach((k, v) -> {
			processRoles(roles, null, v);
		});

		// Start roles sequentially

		int succeeded = 0;
		int index = 0;

		AbstractEventDelegate eventDelegate = AbstractEventDelegate.getInstance();

		while (index < roles.size()) {

			ObjectWrapper<RoleInitEvent> result = new ObjectWrapper<>();

			Thread current = Thread.currentThread();

			eventDelegate.one(RoleInitEvent.class, (evt) -> {
				result.set(evt);

				current.interrupt();
			});

			AbstractRole role = roles.get(index);

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

					if (masterNodeRole == null && AbstractMasterRole.class.isAssignableFrom(role.getClass())) {
						masterNodeRole = (AbstractMasterRole) role;
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

	private static boolean processRoles(List<AbstractRole> accumulator, String dependants, AbstractRole role) {

		for (Class<? extends AbstractRole> c : role.dependencies()) {

			AbstractRole r = com.re.paas.internal.classes.ClassUtil.createInstance(c);

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
	public Map<String, AbstractRole> getAllRoles() {
		return allRoles;
	}

	public Map<String, AbstractRole> getNodeRoles() {
		return nodeRoles;
	}

	@Override
	public void shutdown() {
		
		nodeRoles.values().forEach(role -> {
			role.stop();
		});
	}

	@Override
	public AbstractMasterRole getMasterRole() {
		return masterNodeRole;
	}
}