package com.re.paas.internal.models.listables;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.Map;

import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.IndexedNameSpec;
import com.re.paas.api.listable.Listable;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.ListingType;
import com.re.paas.api.listable.SearchableUISpec;
import com.re.paas.internal.classes.spec.BaseUserSpec;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.tables.defs.users.BaseUserTable;

public class BaseUsersList extends Listable<BaseUserSpec>{

	@Override
	public IndexedNameTypes type() {
		return IndexedNameTypes.USER;
	}

	@Override
	public boolean authenticate(ListingType type, Long userId, List<ListingFilter> listingFilters) {
		return RoleModel.isAccessAllowed(BaseUserModel.getRole(userId), UserFunctionalities.GET_USER_PROFILE);
	}

	@Override
	public Class<BaseUserTable> entityType() {
		return BaseUserTable.class;
	}
	
	@Override
	public boolean searchable() {
		return true;
	}

	@Override
	public Map<String, BaseUserSpec> getAll(List<String> keys) {
		
		Map<String, BaseUserSpec> result = new FluentHashMap<>();
		
		keys.forEach(k -> {
			Long userId = Long.parseLong(k);
			
			BaseUserTable e = ofy().load().type(BaseUserTable.class).id(userId).safe();
			
			IndexedNameSpec nameSpec = new IndexedNameSpec()
						.setKey(userId.toString())
						.setX(e.getFirstName())
						.setY(e.getLastName())
						.setZ(e.getMiddleName());
			
			BaseUserSpec spec = new BaseUserSpec()
					.setId(userId)
					.setRole(e.getRole())
					.setName(nameSpec)
					.setDateCreated(e.getDateCreated())
					.setDateUpdated(e.getDateUpdated());	
			
			result.put(k, spec);
		});
		
		return result;
	}
	
	@Override
	public SearchableUISpec searchableUiSpec() {
		return new SearchableUISpec().setName("users").setListingPageUrl("/users-search");
	}

}
