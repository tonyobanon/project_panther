package com.re.paas.api.roles;

import java.util.List;
import java.util.Map;

import com.re.paas.api.Singleton;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.events.AbstractEventDelegate;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.internal.roles.RoleInitEvent;

public abstract class AbstractRole extends AbstractResource {

	public AbstractRole() {
		super(SpiType.NODE_ROLE);
	}

	public String toString() {
		return name();
	}

	public static Map<String, AbstractRole> get() {
		return getDelegate().getNodeRoles();
	}

	public static AbstractNodeRoleDelegate getDelegate() {
		return Singleton.get(AbstractNodeRoleDelegate.class);
	}

	public String name() {
		return "cluster-member";
	}

	public List<Class<? extends AbstractRole>> dependencies() {
		return new FluentArrayList<Class<? extends AbstractRole>>();
	}

	public void start() {

		// Notify NodeRole Delegate
		AbstractEventDelegate.getInstance().dispatch(new RoleInitEvent(this.name(), true), false);
	}

	public void stop() {
	}

	public boolean applies() {
		return true;
	}

}
