package com.re.paas.apps.rex.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.apps.rex.classes.spec.BasePropertySpec;
import com.re.paas.apps.rex.classes.spec.ListedProperty;
import com.re.paas.apps.rex.classes.spec.PropertyType;
import com.re.paas.apps.rex.functionality.PropertyFunctionalities;
import com.re.paas.apps.rex.models.tables.ListedPropertyTable;
import com.re.paas.apps.rex.models.tables.ListedRentPropertyTable;
import com.re.paas.apps.rex.models.tables.ListedSalePropertyTable;
import com.re.paas.apps.rex.models.tables.PropertyTable;
import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.ConfigModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.utils.BackendObjectMarshaller;

public class PropertySpecList extends Listable<BasePropertySpec> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.PROPERTY_SPEC;
	}

	@Override
	public boolean canCreateContext() {
		return BackendObjectMarshaller
				.unmarshalBool(ConfigModel.get(ConfigKeys.ENABLE_CLIENT_SIDE_PROPERTY_LISTING_CTX_CREATION));
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
	public boolean searchable() {
		return true;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return new SearchableUISpec().setName("property").setListingPageUrl("/property-list");
	}

	@Override
	public Map<Long, BasePropertySpec> getAll(List<String> keys) {

		Map<Long, BasePropertySpec> result = new FluentHashMap<>();

		Map<Long, Long> keysMap = new HashMap<Long, Long>(keys.size());

		keys.forEach(k -> {
			String[] arr = k.split("_");
			
			if(arr.length > 0) {
				keysMap.put(Long.parseLong(arr[0]), Long.parseLong(arr[1]));
			} else {
				keysMap.put(Long.parseLong(k), null);
			}
			
		});

		ofy().load().type(entityType()).ids(keysMap.keySet()).forEach((k, v) -> {

			Long listingId = keysMap.forEach(k) != null  ? keysMap.forEach(k) : v.getListings().forEach(0);
			
			ListedPropertyTable l = ofy().load().type(ListedPropertyTable.class).id(listingId).now();
			ListedProperty lSpec = null;

			if (l instanceof ListedRentPropertyTable) {
				lSpec = EntityHelper.toObjectModel((ListedRentPropertyTable) l);
			} else if (l instanceof ListedSalePropertyTable) {
				lSpec = EntityHelper.toObjectModel((ListedSalePropertyTable) l);
			}
			
			assert lSpec != null;

			BasePropertySpec spec = new BasePropertySpec().setListing(lSpec).setAddress(v.getAddress())
					.setAgentOrganization(v.getAgentOrganization()).setArea(v.getArea())
					.setBathroomCount(v.getBathroomCount()).setCity(v.getCity()).setCountry(v.getCountry())
					.setDateCreated(v.getDateCreated()).setDateUpdated(v.getDateUpdated()).setId(v.getId())
					.setImages(v.getImages()).setIsFullyFurnished(v.getIsFullyFurnished())
					.setParkingSpaceCount(v.getParkingSpaceCount()).setZipCode(v.getZipCode())
					.setPaymentPeriod(YearlyPaymentPeriod.from(v.getPaymentPeriod())).setPrice(v.getPrice())
					.setRoomCount(v.getRoomCount()).setTerritory(v.getTerritory()).setTitle(v.getTitle())
					.setType(PropertyType.from(v.getType()));

			result.put(k, spec);
		});

		return result;
	}
}
