package com.re.paas.internal.listable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.events.BaseEvent;
import com.re.paas.api.listable.AbstractListableIndexDelegate;
import com.re.paas.api.listable.ListableIndex;
import com.re.paas.api.listable.ListableIndexDeleteEvent;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;

@DelegateSpec(dependencies = {SpiType.EVENT})
public class ListableIndexDelegate extends AbstractListableIndexDelegate {

	private static Logger LOG = Logger.get(ListableIndexDelegate.class);
	
	private static final String DELIMETER = "_";
	private static final String RMAP_NAMESPACE = "li_n";

	@Override
	public DelegateInitResult init() {
		return forEach(this::addListableIndex);
	}

	@Override
	protected void add(List<Class<ListableIndex<?>>> classes) {
		classes.forEach(this::addListableIndex);
	}

	@Override
	protected List<Class<ListableIndex<?>>> remove(List<Class<ListableIndex<?>>> classes) {
		classes.forEach(this::removeListableIndex);
		return Collections.emptyList();
	}
	
	private Map<String, Map<String, ListableIndex<?>>> getResourceMap() {

		@SuppressWarnings("unchecked")
		Map<String, Map<String, ListableIndex<?>>> namespaceMap = (Map<String, Map<String, ListableIndex<?>>>) getLocalStore().get(RMAP_NAMESPACE);
		
		if(namespaceMap == null) {
			namespaceMap = new HashMap<>();
			getLocalStore().put(RMAP_NAMESPACE, namespaceMap);
		}
		
		return namespaceMap;
	}

	@Override
	public Collection<String> getNamespaces() {
		return getResourceMap().keySet();
	}
	
	@Override
	public Map<String, ListableIndex<?>> getListableIndexes(String namespace) {
		return getResourceMap().get(namespace);
	}

	private void addListableIndex(Class<ListableIndex<?>> c) {
		
		ListableIndex<?> listable = com.re.paas.internal.classes.ClassUtil.createInstance(c);

		LOG.info("Registering listable index: " + listable.asString());

		Map<String, ListableIndex<?>> namespaceMap = (Map<String, ListableIndex<?>>) getListableIndexes(listable.namespace());

		if (namespaceMap == null) {
			namespaceMap = new HashMap<>();
			getLocalStore().put(listable.namespace(), namespaceMap);
		}
		
		if (namespaceMap.containsKey(listable.id())) {
			throw new RuntimeException(
					"Namespace: " + listable.namespace() + " already contains id: " + listable.id() + " => " + c.getName());
		}
				
		namespaceMap.put(listable.id(), listable);
	}

	private void removeListableIndex(Class<ListableIndex<?>> c) {

		ListableIndex<?> listable = com.re.paas.internal.classes.ClassUtil.createInstance(c);
		
		LOG.info("Dispatching ListableIndexDeleteEvent for " + listable.asString());
		
		// Notify platform of this impending delete
		BaseEvent.getDelegate().dispatch(new ListableIndexDeleteEvent(listable), false);
		
		Map<String, ListableIndex<?>> namespaceMap = (Map<String, ListableIndex<?>>) getListableIndexes(listable.namespace());
		namespaceMap.remove(listable.id());

		if (namespaceMap.isEmpty()) {
			getResourceMap().remove(listable.namespace());
		}
		
		LOG.info("Removed listable index: " + listable.asString());
	}

	@Override
	public String toString(ListableIndex<?> type) {
		return type.namespace() + DELIMETER + type.id();
	}

	@Override
	public ListableIndex<?> fromString(String typeString) {
		
		String[] arr = typeString.split(DELIMETER);
		
		Map<String, ListableIndex<?>> namespaceMap = (Map<String, ListableIndex<?>>) getListableIndexes(arr[0]);
		
		return namespaceMap.get(arr[1]);
	}

}
