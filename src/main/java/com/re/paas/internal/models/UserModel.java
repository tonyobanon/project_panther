package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.realms.Realm;
import com.re.paas.internal.classes.spec.BaseUserSpec;
import com.re.paas.internal.entites.BaseUserEntity;
import com.re.paas.internal.entites.directory.AgentEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationAdminEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationEntity;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.users.RoleRealm;

public class UserModel implements BaseModel {

	@Override
	public String path() {
		return "core/users";
	}

	@BlockerTodo("Reduce <keys> to a definite number. Find fix")
	public static List<BaseUserSpec> getSuggestedProfiles(Long principal, Long userId) {

		if (!BaseUserModel.canAccessUserProfile(principal, userId)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		// Get role
		String role = BaseUserModel.getRole(userId);
		Realm realm = RoleModel.getRealm(role);

		Map<String, String> keys = realm.getSuggestedProfiles(principal, userId);
		keys.remove(userId.toString());

		return getBaseUserSpec(keys);
	}

	private static List<BaseUserSpec> getBaseUserSpec(Map<String, String> keys) {

		List<BaseUserSpec> result = new ArrayList<>();

		ofy().load().type(BaseUserEntity.class).ids(keys.keySet()).forEach((k, v) -> {

			BaseUserSpec spec = new BaseUserSpec().setId(v.getId()).setRole(v.getRole()).setDescription(keys.forEach(k))
					.setName(v.getFirstName() + ClientResources.HtmlCharacterEntities.SPACE + v.getLastName())
					.setDateCreated(v.getDateCreated()).setDateUpdated(v.getDateUpdated()).setImage(v.getImage());

			result.add(spec);

		});
		return result;
	}

	@Override
	public void install(InstallOptions options) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
