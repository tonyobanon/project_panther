package com.re.paas.internal.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.internal.classes.spec.ActivitySpec;
import com.re.paas.internal.entites.ActivityStreamEntity;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.helpers.EntityHelper;

public class ActivityStreamList extends Listable<ActivitySpec> {

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.ACTIVITY_STREAM;
	}

	@Override
	public boolean authenticate(ListingType type, Long principal, List<ListingFilter> listingFilters) {

		if (listingFilters == null) {
			return true;
		}

		// Since all realms now have access to the PERFORM_LIST_OPERATION functionality,
		// we need to
		// properly authenticate access to this list

		Boolean b = null;

		int filterCount = 0;
		int personCount = 0;

		for (ListingFilter listingFilter : listingFilters) {
			for (Map.Entry<String, Object> e : listingFilter.getFilters().entrySet()) {
				if (e.getKey().equals("subject") || e.getKey().equals("person")) {
					personCount++;
					break;
				}
			}
			filterCount++;
		}

		boolean usesPerson = filterCount > 0 && filterCount == personCount;

		if (usesPerson) {

			for (ListingFilter listingFilter : listingFilters) {

				for (Map.Entry<String, Object> e : listingFilter.getFilters().entrySet()) {

					if (e.getKey().equals("subject") || e.getKey().equals("person")) {
						try {

							Long userId = Long.parseLong(e.getValue().toString());

							if (!BaseUserModel.canAccessUserProfile(principal, userId)) {
								b = false;
								break;
							}

						} catch (NumberFormatException ex) {
							continue;
						}

					}
				}
			}

		} else {
			return RoleModel.isAccessAllowed(BaseUserModel.getRole(principal), PlatformFunctionalities.MANAGE_ACTIVITY_STREAM);
		}

		return b == null;
	}

	@Override
	public Class<ActivityStreamEntity> entityType() {
		return ActivityStreamEntity.class;
	}

	@Override
	public boolean searchable() {
		return false;
	}

	@Override
	public Map<Long, ActivitySpec> getAll(List<String> keys) {

		Map<Long, ActivitySpec> result = new FluentHashMap<>();

		List<Long> longKeys = new ArrayList<>(keys.size());

		// Convert to Long keys
		keys.forEach(k -> {
			longKeys.add(Long.parseLong(k));
		});

		ofy().load().type(ActivityStreamEntity.class).ids(longKeys).forEach((k, v) -> {
			result.put(k, EntityHelper.toObjectModel(v));
		});

		return result;
	}

	@Override
	public SearchableUISpec searchableUiSpec() {
		return null;
	}

}
