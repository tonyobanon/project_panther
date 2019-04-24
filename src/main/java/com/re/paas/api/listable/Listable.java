package com.re.paas.api.listable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.runtime.spi.AbstractResource;
import com.re.paas.api.runtime.spi.SpiType;

public abstract class Listable<S> extends AbstractResource {
	
	public Listable() {
		super(SpiType.LISTABLE);
	}
	
	public static AbstractListableDelegate getDelegate() {
		return Singleton.get(AbstractListableDelegate.class);
	}

	public abstract IndexedNameType type();

	/**
	 * This retrieves a set of objects. It is advised to use a batch get operation
	 * where possible, to reduce costs.
	 */
	public abstract Map<?, S> getAll(List<String> keys);

	/**
	 * This authenticates the user that wants to access this data table
	 */
	public abstract boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters);

	public abstract Class<?> entityType();

	public abstract boolean searchable();

	public SearchableUISpec searchableUiSpec() {
		return null;
	}
	
	public List<ListingFilter> defaultListingFilters() {
		return new ArrayList<>();
	}
	
	public boolean canCreateContext() {
		return true;
	}

}
