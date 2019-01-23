package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.designpatterns.Singleton;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.apps.rex.realms.AgentRealm;
import com.re.paas.apps.rex.realms.OrganizationAdminRealm;
import com.re.paas.internal.entites.BaseUserEntity;
import com.re.paas.internal.entites.UserRoleEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.errors.RolesError;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.realms.AdminRealm;

public class RoleModel implements BaseModel {

	@Override
	public String path() {
		return "core/roles";
	}

	@Override
	public void preInstall() {

		// Create default roles
		Logger.get().info("Creating default roles");

		newRole("Admin", true, new AdminRealm());
		newRole("Organization Admin", true, new OrganizationAdminRealm());
		newRole("Agent", true, new AgentRealm());
	}

	@Override
	public void start() {

		// Register role functionalities in realm delegate
		
		AbstractRealmDelegate delegate = Realm.getDelegate();

		for (String realmName : delegate.getRealmNames()) {

			Realm realm = delegate.getRealm(realmName);

			for (String role : listRoles(realm)) {

				Collection<Functionality> functionalities = new ArrayList<>();

				for (String fString : fetchRoleFunctionalities(role)) {

					Functionality f = Functionality.fromString(fString);

					if (f == null) {

						// This functionality has been removed from the realm
						// so we need to remove from db as well
						updateRoleSpec(role, false, f, false);
					}

					functionalities.add(f);
				}

				delegate.addRoleFunctionalities(role, functionalities);
			}
		}
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void newRole(String name, Realm realm) {
		newRole(name, false, realm);
	}

	protected static void newRole(String name, Boolean isDefault, Realm realm) {
		Logger.get().info("Creating role: " + name);
		ofy().save().entity(new UserRoleEntity().setName(name).setIsDefault(isDefault).setRealm(realm.name())
				.setSpec(new ArrayList<>()).setDateCreated(Dates.now())).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void deleteRole(String name) {

		if (isRoleInUse(name)) {
			throw new PlatformException(RolesError.ROLE_IN_USE_AND_CANNOT_BE_DELETED);
		}

		if (isDefaultRole(name)) {
			throw new PlatformException(RolesError.DEFAULT_ROLE_CANNOT_BE_DELETED);
		}

		ofy().delete().key(Key.create(UserRoleEntity.class, name)).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Map<String, String> listRoles() {

		Map<String, String> result = new FluentHashMap<>();

		ofy().load().type(UserRoleEntity.class).list().forEach(o -> {
			result.put(o.getName(), o.getRealm());
		});

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Collection<String> listRoles(Realm realm) {

		Collection<String> roles = new ArrayList<>();

		Query<UserRoleEntity> query = ofy().load().type(UserRoleEntity.class).filter("realm =", realm.getValue());
		for (UserRoleEntity role : query) {

			roles.add(role.getName());
		}

		return roles;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Map<String, Integer> getUsersCount(List<String> names) {
		Map<String, Integer> result = new FluentHashMap<>();
		names.forEach(name -> {
			Integer count = EntityUtils.query(BaseUserEntity.class, QueryFilter.get("role =", name)).size();
			result.put(name, count);
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.GET_ROLE_REALMS)
	public static Collection<String> listRealms() {
		return Realm.getDelegate().getRealmNames();
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Map<String, String> getRealmFunctionalities(Realm realm) {

		Map<String, String> result = new FluentHashMap<>();

		Collection<Functionality> f = Realm.getDelegate().getFunctionalities(realm);

		f.forEach(k -> {
			if (k.isVisible()) {
				result.put(k.asString(), k.getName());
			}
		});
		return result;
	}

	private static Collection<String> fetchRoleFunctionalities(String name) {
		List<String> result = new ArrayList<>();
		UserRoleEntity entity = ofy().load().type(UserRoleEntity.class).id(name).safe();
		entity.getSpec().forEach(f -> {
			result.add(f);
		});
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Collection<String> getRoleFunctionalities(String name) {
		return Realm.getDelegate().getRoleFunctionalitiesAstring(name);
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void updateRoleSpec(String name, Boolean add, Functionality f) {
		updateRoleSpec(name, add, f, true);
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	private static void updateRoleSpec(String name, Boolean add, Functionality f, boolean updateDelegate) {

		UserRoleEntity entity = ofy().load().type(UserRoleEntity.class).id(name).safe();
		List<String> functions = entity.getSpec();

		AbstractRealmDelegate delegate = Realm.getDelegate();

		if (add) {
			functions.add(f.asString());
			if (updateDelegate) {
				delegate.addRoleFunctionalities(name, Lists.newArrayList(f));
			}
		} else {
			functions.remove(f.asString());
			if (updateDelegate) {
				delegate.removeRoleFunctionalities(name, Lists.newArrayList(f));
			}
		}

		entity.setSpec(functions);
		ofy().save().entity(entity).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.GET_ROLE_REALMS)
	public static Realm getRealm(String name) {
		UserRoleEntity entity = ofy().load().type(UserRoleEntity.class).id(name).safe();
		return Realm.get(entity.getRealm());
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static String getDefaultRole(Realm realm) {
		return ofy().load().type(UserRoleEntity.class).filter("realm = ", realm.name()).filter("isDefault", true)
				.first().safe().getName();
	}

	private static boolean isRoleInUse(String name) {
		return !EntityUtils.query(BaseUserEntity.class, QueryFilter.get("role =", name)).isEmpty();
	}

	private static boolean isDefaultRole(String name) {
		UserRoleEntity entity = ofy().load().type(UserRoleEntity.class).id(name).safe();
		return entity.getIsDefault();
	}

	protected static boolean isRoleValid(String name) {
		try {
			return getRealm(name) != null;
		} catch (NotFoundException e) {
			return false;
		}
	}

	public static boolean isAccessAllowed(String roleName, Functionality... functionalities) {

		Collection<String> Userfunctionalities = getRoleFunctionalities(roleName);

		for (Functionality f : functionalities) {
			if (!Userfunctionalities.contains(f.asString())) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub

	}

}
