package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.BOOL;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.L;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.attribute_not_exists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Index;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.models.errors.RolesError;
import com.re.paas.integrated.realms.AdminRealm;
import com.re.paas.integrated.tables.defs.users.BaseUserTable;
import com.re.paas.integrated.tables.defs.users.UserRoleTable;
import com.re.paas.integrated.tables.spec.users.UserRoleTableSpec;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.integrated.fusion.functionalities.RoleFunctionalities;

public class RoleModel extends BaseModel {

	@Override
	public String path() {
		return "core/roles";
	}

	@Override
	public void preInstall() {

		// Create default roles
		Logger.get().info("Creating default roles");

		// WTF?

		newRole("Admin", true, new AdminRealm());
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

					assert f != null;

					functionalities.add(f);
				}

				delegate.addRoleFunctionalities(role, functionalities);
				delegate.addRoleRealm(role, realm);
			}
		}
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void newRole(String name, Realm realm) {
		newRole(name, false, realm);
	}

	protected static void newRole(String name, Boolean isDefault, Realm realm) {
		Logger.get().info("Creating role: " + name);

		Item item = new Item().withString(UserRoleTableSpec.NAME, name);
		Date now = Dates.now();

		if (isDefault) {
			item.withBoolean(UserRoleTableSpec.IS_DEFAULT, isDefault);
		}

		item.withString(UserRoleTableSpec.REALM, realm.name()).withList(UserRoleTableSpec.SPEC, Collections.emptyList())
				.withDate(UserRoleTableSpec.DATE_CREATED, now).withDate(UserRoleTableSpec.DATE_UPDATED, now);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder()
				.withCondition(attribute_not_exists(UserRoleTableSpec.NAME));

		Database.get().getTable(UserRoleTable.class).putItem(expr.buildForPut().withItem(item));

		ConfigModel.putInt(ConfigKeys.USER_COUNT_$ROLE.replace("$ROLE", name), 0);
		
		AbstractRealmDelegate delegate = Realm.getDelegate();
		delegate.addRoleRealm(name, realm);
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static void deleteRole(String name) {

		if (isRoleInUse(name)) {
			throw new PlatformException(RolesError.ROLE_IN_USE_AND_CANNOT_BE_DELETED);
		}

		if (isDefaultRole(name)) {
			throw new PlatformException(RolesError.DEFAULT_ROLE_CANNOT_BE_DELETED);
		}

		Database.get().getTable(UserRoleTable.class).deleteItem(DeleteItemSpec.forKey(UserRoleTableSpec.NAME, name));
		
		AbstractRealmDelegate delegate = Realm.getDelegate();
		
		delegate.removeRoleFunctionalities(name);
		delegate.removeRoleRealm(name);
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Map<String, String> listRoles() {

		Map<String, String> result = new FluentHashMap<>();

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(UserRoleTableSpec.NAME);

		Database.get().getTable(UserRoleTable.class).getIndex(UserRoleTableSpec.REALM_INDEX).all(expr.buildForScan())
				.forEach(i -> {
					result.put(i.getString(UserRoleTableSpec.NAME), i.getString(UserRoleTableSpec.REALM));
				});

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Collection<String> listRoles(Realm realm) {

		Collection<String> roles = new ArrayList<>();

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder()
				.withKeyCondition(S(UserRoleTableSpec.REALM).eq(realm.name())).addProjection(UserRoleTableSpec.NAME);

		Database.get().getTable(UserRoleTable.class).getIndex(UserRoleTableSpec.REALM_INDEX).all(expr.buildForQuery())
				.forEach(i -> {
					roles.add(i.getString(UserRoleTableSpec.NAME));
				});

		return roles;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static Map<String, Integer> getUsersCount(List<String> names) {

		Map<String, Integer> result = new FluentHashMap<>();

		names.forEach(name -> {
			Integer count = EntityUtils.query(BaseUserTable.class, QueryFilter.get("role =", name)).size();
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

	private static List<String> fetchRoleFunctionalities(String name) {
		return get(name, UserRoleTableSpec.SPEC).getList(UserRoleTableSpec.SPEC).stream().map(s -> ((String) s))
				.collect(Collectors.toList());
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
	@Todo("Use list_append with if_exists for adds, and find a solution for removes.")
	private static void updateRoleSpec(String name, Boolean add, Functionality f, Boolean updateDelegate) {

		List<String> functionalities = fetchRoleFunctionalities(name);
		AbstractRealmDelegate delegate = Realm.getDelegate();

		if (add) {
			functionalities.add(f.asString());
			if (updateDelegate) {
				delegate.addRoleFunctionalities(name, Lists.newArrayList(f));
			}
		} else {
			functionalities.remove(f.asString());
			if (updateDelegate) {
				delegate.removeRoleFunctionalities(name, Lists.newArrayList(f));
			}
		}

		Table t = Database.get().getTable(UserRoleTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(S(UserRoleTableSpec.NAME).eq(name))
				.addUpdate(L(UserRoleTableSpec.SPEC).set(functionalities))
				.addUpdate(D(UserRoleTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.GET_ROLE_REALMS)
	public static Realm getRealm(String name) {
		return Realm.get(get(name, UserRoleTableSpec.REALM).getString(UserRoleTableSpec.REALM));
	}

	public static Map<String, Realm> getRealms(Collection<String> names) {
		Map<String, Realm> result = new HashMap<String, Realm>(names.size());
		for (String name : names) {
			result.put(name, getRealm(name));
		}
		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.Constants.MANAGE_ROLES)
	public static String getDefaultRole(Realm realm) {

		Index index = Database.get().getTable(UserRoleTable.class).getIndex(UserRoleTableSpec.REALM_INDEX);

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(UserRoleTableSpec.NAME)

				.withKeyCondition(
						S(UserRoleTableSpec.REALM).eq(realm.name()).and(BOOL(UserRoleTableSpec.IS_DEFAULT).eq(true)));

		return index.first(expr.buildForQuery()).getString(UserRoleTableSpec.NAME);
	}

	private static boolean isRoleInUse(String name) {
		return !EntityUtils.query(BaseUserTable.class, QueryFilter.get("role =", name)).isEmpty();
	}

	private static Boolean isDefaultRole(String name) {
		return get(name, UserRoleTableSpec.IS_DEFAULT).getBoolean(UserRoleTableSpec.IS_DEFAULT);
	}

	protected static boolean isRoleValid(String name) {
		return get(name, UserRoleTableSpec.NAME) != null;
	}

	private static Item get(String name, String... projections) {

		Table t = Database.get().getTable(UserRoleTable.class);
		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(projections)
				.withCondition(S(UserRoleTableSpec.NAME).eq(name));

		GetItemSpec spec = expr.buildForGetItem();
		return t.getItem(spec);
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
