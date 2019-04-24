package com.re.paas.internal.models.listables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.listable.AbstractListableDelegate;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;

@DelegateSpec(dependencies = {SpiType.INDEXED_NAME_TYPE})
public class ListableDelegate extends AbstractListableDelegate {
 
	private static Map<String, Listable<?>> listables = new HashMap<>();
	private static Map<String, SearchableUISpec> searchables = new HashMap<>();

	@Override
	public DelegateInitResult init() {
		Consumer<Class<Listable<?>>> consumer = (c) -> {
			addListable(c);
		};
		forEach(consumer);
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected void add(List<Class<Listable<?>>> classes) {
		classes.forEach(c -> {
			addListable(c);
		});
	}

	@Override
	protected void remove(List<Class<Listable<?>>> classes) {
		classes.forEach(c -> {
			removeListable(c);
		});
	}

	private static final void addListable(Class<Listable<?>> c) {
		Listable<?> instance = ClassUtils.createInstance(c);
		listables.put(instance.type().asString(), instance);
		if (instance.searchable()) {
			searchables.put(instance.type().asString(), instance.searchableUiSpec());
		}
	}

	@BlockerTodo("We need to remove existing cached entries for the retired listables")
	private static final void removeListable(Class<Listable<?>> c) {
		Listable<?> instance = ClassUtils.createInstance(c);
		listables.remove(instance.type().asString());
		if (instance.searchable()) {
			searchables.remove(instance.type().asString());
		}
	}
	
	@Override
	public void forEachSearchable(Consumer<IndexedNameType> consumer) {
		searchables.keySet().forEach(i -> {
			consumer.accept(IndexedNameType.fromString(i));
		});
	}
	
	@Override
	public Listable<?> getListable(IndexedNameType type) {
		return listables.get(type.asString());
	}

	@Override
	public SearchableUISpec getSearchable(IndexedNameType type) {
		return searchables.get(type.asString());
	}

}
