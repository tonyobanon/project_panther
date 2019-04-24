package com.re.paas.apps.rex.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.apps.rex.classes.spec.BasePropertyDescriptor;
import com.re.paas.apps.rex.classes.spec.PropertyType;
import com.re.paas.apps.rex.functionality.PropertyFunctionalities;
import com.re.paas.apps.rex.models.tables.PropertyTable;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;

public class PropertyDescriptorList extends Listable<BasePropertyDescriptor> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.PROPERTY_DESCRIPTOR;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return RoleModel.isAccessAllowed(BaseUserModel.getRole(userId), PropertyFunctionalities.LIST_PROPERTY);
	}

	@Override
	public Class<PropertyTable> entityType() {
		return PropertyTable.class;
	}

	@Override
	public Map<Long, BasePropertyDescriptor> getAll(List<String> keys) {

		Map<Long, BasePropertyDescriptor> result = new FluentHashMap<>();

		List<Long> longKeys = new ArrayList<>(keys.size());

		// Convert to Long keys
		keys.forEach(k -> {
			longKeys.add(Long.parseLong(k));
		});

		ofy().load().type(entityType()).ids(longKeys).forEach((k, v) -> {

			BasePropertyDescriptor spec = new BasePropertyDescriptor()
					.setAddress(v.getAddress())
					.setAgentOrganization(v.getAgentOrganization())
					.setArea(v.getArea())
					.setCity(v.getCity())
					.setCountry(v.getCountry())
					.setDateCreated(v.getDateCreated())
					.setDateUpdated(v.getDateUpdated())
					.setId(v.getId())
					.setPrice(v.getPrice())
					.setCurrency(v.getCurrency())
					.setTerritory(v.getTerritory())
					.setTitle(v.getTitle())
					.setType(PropertyType.from(v.getType()))
					.setZipCode(v.getZipCode());
					
			result.put(k, spec);
		});

		return result;
	}

	@Override
	public boolean searchable() {
		return false;
	}
}
