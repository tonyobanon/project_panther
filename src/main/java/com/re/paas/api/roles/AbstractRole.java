package com.re.paas.api.roles;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.clustering.classes.MemberStatus;
import com.re.paas.api.clustering.events.MemberJoinEvent;
import com.re.paas.api.clustering.events.MemberLeaveEvent;
import com.re.paas.api.clustering.events.MemberStateChangeEvent;
import com.re.paas.api.designpatterns.Singleton;
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

	/**
	 * This callback method, invoked when a new member joins the cluster. Note: that
	 * the member in question may not have been fully initialized when this called,
	 * and will have a status of {@link MemberStatus#STARTING}. Use
	 * {@link AbstractRole#onMemberStateChange(MemberStateChangeEvent)} to know when
	 * the member is fully online
	 * 
	 * @param evt
	 */
	public void onMemberJoin(MemberJoinEvent evt) {
	}

	/**
	 * This callback method, invoked a member leaves the cluster.
	 * 
	 * @param evt
	 */
	public void onMemberLeave(MemberLeaveEvent evt) {
	}

	/**
	 * This callback method, invoked a member state changes.
	 * 
	 * @param evt
	 */
	public void onMemberStateChange(MemberStateChangeEvent evt) {
	}

}
