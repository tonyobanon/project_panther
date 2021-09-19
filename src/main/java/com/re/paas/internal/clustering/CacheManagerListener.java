package com.re.paas.internal.clustering;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;
import org.infinispan.remoting.transport.jgroups.JGroupsAddress;

@Listener
public class CacheManagerListener {

	public void viewChanged(ViewChangedEvent evt) {
		
		System.out.println("Received ViewChangedEvent");
		
		
		System.out.println("----- Old Members ------");
		
		evt.getOldMembers().forEach(addr -> {
			System.out.println(addr + " or " + ((JGroupsAddress)addr));
		});
		
		System.out.println("----- New Members ------");
		
		evt.getNewMembers().forEach(addr -> {
			System.out.println(addr + " or " + ((JGroupsAddress)addr));
		});
		
		
	}
	
}
