package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.PrimaryKey;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.GetItemsSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BatchGetItemRequest;
import com.re.paas.api.infra.database.model.ReturnValue;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.Gender;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.sentences.Article;
import com.re.paas.api.sentences.CustomPredicate;
import com.re.paas.api.sentences.ObjectEntity;
import com.re.paas.api.sentences.Preposition;
import com.re.paas.api.sentences.Sentence;
import com.re.paas.api.sentences.SubjectEntity;
import com.re.paas.api.utils.Dates;
import com.re.paas.integrated.fusion.functionalities.AuthFunctionalities;
import com.re.paas.integrated.fusion.functionalities.UserFunctionalities;
import com.re.paas.integrated.models.errors.UserAccountError;
import com.re.paas.integrated.realms.AdminRealm;
import com.re.paas.integrated.tables.defs.payments.BillingContextTable;
import com.re.paas.integrated.tables.defs.users.BaseUserTable;
import com.re.paas.integrated.tables.defs.users.UserFormValueTable;
import com.re.paas.integrated.tables.spec.payments.BillingContextTableSpec;
import com.re.paas.integrated.tables.spec.users.BaseUserTableSpec;
import com.re.paas.integrated.tables.spec.users.UserFormValueTableSpec;
import com.re.paas.internal.classes.Json;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.fusion.Unexposed;
import com.re.paas.internal.sentences.ObjectTypes;
import com.re.paas.internal.sentences.SubjectTypes;

@BlockerTodo("stop storing passwords as plain text, hash it instead")
@Model(dependencies = RoleModel.class)
public class BaseUserModel extends BaseModel {
	
	public static final String USER_ID_SEARCH_FIELD = "USER_ID";
	public static final String PERSON_NAME_SEARCH_FIELD = "PERSON_NAME";
	
	@Override
	public String path() {
		return "core/base-user";
	}

	@Override
	public void preInstall() {

		ConfigModel.putInt(ConfigKeys.USER_COUNT_ARCHIVE, 0);
		ConfigModel.putInt(ConfigKeys.USER_COUNT_CURRENT, 0);
	}

	@Override
	public void install(InstallOptions options) {

		for (UserProfileSpec spec : options.getAdmins()) {
			registerUser(spec, RoleModel.getDefaultRole(new AdminRealm()), -1l);
		}
	}

	private static Long nextKey() {
		return (Long) ConfigModel.incr(ConfigKeys.USER_COUNT_ARCHIVE);
	}

	@BlockerTodo("Really, deleting a user is much more complex than this")
	protected static void deleteUser(Long id) {

		// Delete form values
		deleteFieldValuesForUser(id);

		// Delete entry

		Table t = Database.get().getTable(BaseUserTable.class);

		DeleteItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(id))
				.buildForDeleteItem().withReturnValues(ReturnValue.ALL_OLD);

		Item i = t.deleteItem(spec).getItem();

		// Update metric

		ConfigModel.decr(ConfigKeys.USER_COUNT_CURRENT);
		
		ConfigModel.decr(ConfigKeys.USER_COUNT_$ROLE.replace("$ROLE", i.getString(BaseUserTableSpec.ROLE)));

		// Update cached list index
		SearchModel.removeCachedListKey(IndexedNameTypes.USER, id);
	}

	public static Long getUserId(String email) {

		Table t = Database.get().getTable(BaseUserTable.class);

		QuerySpec spec = new ExpressionSpecBuilder().withKeyCondition(S(BaseUserTableSpec.EMAIL).eq(email))
				.addProjection(BaseUserTableSpec.ID).buildForQuery();

		Item i = t.getIndex(BaseUserTableSpec.EMAIL_INDEX).first(spec);

		return i != null ? i.getLong(BaseUserTableSpec.ID) : null;
	}

	@ModelMethod(functionality = AuthFunctionalities.Constants.EMAIL_LOGIN_USER)
	public static Long loginByEmail(String email, String password) {

		Table t = Database.get().getTable(BaseUserTable.class);

		QuerySpec spec = new ExpressionSpecBuilder().withKeyCondition(S(BaseUserTableSpec.EMAIL).eq(email))
				.addProjection(BaseUserTableSpec.PASSWORD).addProjection(BaseUserTableSpec.ID).buildForQuery();

		Item i = t.getIndex(BaseUserTableSpec.EMAIL_INDEX).first(spec);

		if (i == null) {
			// Incorrect email
			throw new PlatformException(UserAccountError.EMAIL_DOES_NOT_EXIST);
		}

		if (!i.getString(BaseUserTableSpec.PASSWORD).equals(password)) {
			// Wrong password
			throw new PlatformException(UserAccountError.INCORRECT_PASSWORD);
		}

		return i.getLong(BaseUserTableSpec.ID);
	}

	@ModelMethod(functionality = AuthFunctionalities.Constants.PHONE_LOGIN_USER)
	public static Long loginByPhone(String phone, String password) {

		Table t = Database.get().getTable(BaseUserTable.class);

		QuerySpec spec = new ExpressionSpecBuilder().withKeyCondition(S(BaseUserTableSpec.PHONE).eq(phone))
				.addProjection(BaseUserTableSpec.PASSWORD).addProjection(BaseUserTableSpec.ID).buildForQuery();

		Item i = t.getIndex(BaseUserTableSpec.PHONE_INDEX).first(spec);

		if (i == null) {
			// Incorrect email
			throw new PlatformException(UserAccountError.PHONE_DOES_NOT_EXIST);
		}

		if (!i.getString(BaseUserTableSpec.PASSWORD).equals(password)) {
			// Wrong password
			throw new PlatformException(UserAccountError.INCORRECT_PASSWORD);
		}

		return i.getLong(BaseUserTableSpec.ID);
	}

	@Todo("Validate user's phone number, and other info properly, make method protected")
	@BlockerTodo("Long id = nextKey(); may be problematic due to collisions, fix")
	@BlockerTodo("The address should conform to NUMBER, STREET [SUITE AVENUE], see BillingModel.createPaymentRequest(..)")

	public static Long registerUser(UserProfileSpec spec, String role, Long principal) {

		if (doesEmailExist(spec.getEmail())) {
			throw new PlatformException(UserAccountError.EMAIL_ALREADY_EXISTS);
		}

		if (doesPhoneExist(spec.getPhone())) {
			throw new PlatformException(UserAccountError.PHONE_ALREADY_EXISTS);
		}

		Long id = nextKey();

		Table t = Database.get().getTable(BaseUserTable.class);

		Item i = Item.fromMap(Json.toMap(spec));

		// Update item with id, role and principal
		i.with(BaseUserTableSpec.ID, id).with(BaseUserTableSpec.ROLE, role).with(BaseUserTableSpec.PRINCIPAL, principal)
				.with(BaseUserTableSpec.DATE_CREATED, Dates.now()).with(BaseUserTableSpec.DATE_UPDATED, Dates.now());

		t.putItem(PutItemSpec.forItem(i));

		// Create new billing context for account

		Database.get().getTable(BillingContextTable.class)
				.putItem(PutItemSpec.forItem(new Item().with(BillingContextTableSpec.ACCOUNT_ID, id)));

		// Update system config / metrics

		ConfigModel.incr(ConfigKeys.USER_COUNT_CURRENT);
		
		ConfigModel.incr(ConfigKeys.USER_COUNT_$ROLE.replace("$ROLE", role));
		
		// Update statsd, i.e StatsdBuckets.USERS_COUNT

		// Update cached list index
		SearchModel.addCachedListKey(IndexedNameTypes.USER, id);

		return id;
	}

	private static boolean doesEmailExist(String email) {

		Table t = Database.get().getTable(BaseUserTable.class);

		QuerySpec spec = new ExpressionSpecBuilder().withKeyCondition(S(BaseUserTableSpec.EMAIL).eq(email))
				.buildForQuery();

		Item i = t.getIndex(BaseUserTableSpec.EMAIL_INDEX).first(spec);
		return i != null;
	}

	private static boolean doesPhoneExist(String phone) {

		Table t = Database.get().getTable(BaseUserTable.class);

		QuerySpec spec = new ExpressionSpecBuilder().withKeyCondition(S(BaseUserTableSpec.PHONE).eq(phone))
				.buildForQuery();

		Item i = t.getIndex(BaseUserTableSpec.PHONE_INDEX).first(spec);
		return i != null;
	}

	private static Item get(Long userId, String... projections) {

		Table t = Database.get().getTable(BaseUserTable.class);
		ExpressionSpecBuilder expr = new ExpressionSpecBuilder().addProjection(projections)
				.withCondition(N(BaseUserTableSpec.ID).eq(userId));

		GetItemSpec spec = expr.buildForGetItem();
		return t.getItem(spec);
	}

	private static String get(Long userId, String projection) {
		return get(userId, new String[] { projection }).get(projection).toString();
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.VIEW_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static String getAvatar(Long userId) {
		return userId.equals(-1l) ? null : get(userId, BaseUserTableSpec.IMAGE);
	}

	protected static Boolean isMaleUser(Long userId) {
		return Gender.from(Integer.parseInt(get(userId, BaseUserTableSpec.GENDER))).equals(Gender.MALE);
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public static boolean canAccessUserProfile(Long principal, Long userId) {

		Realm principalRealm = RoleModel.getRealm(getRole(principal));
		Realm userRealm = RoleModel.getRealm(getRole(userId));

		return principalRealm.authority() >= userRealm.authority();
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public static UserProfileSpec getProfile(Long principal, Long userId, String... projections) {

		if (principal != null && !canAccessUserProfile(principal, userId)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		Item i = get(userId, projections);

		UserProfileSpec spec = new UserProfileSpec().setId(i.getLong(BaseUserTableSpec.ID))
				.setApplicationId(i.getLong(BaseUserTableSpec.APPLICATION_ID))
				.setEmail(i.getString(BaseUserTableSpec.EMAIL)).setFirstName(i.getString(BaseUserTableSpec.FIRST_NAME))
				.setMiddleName(i.getString(BaseUserTableSpec.MIDDLE_NAME))
				.setLastName(i.getString(BaseUserTableSpec.LAST_NAME)).setImage(i.getString(BaseUserTableSpec.IMAGE))
				.setPhone(i.getString(BaseUserTableSpec.PHONE))
				.setDateOfBirth(i.getDate(BaseUserTableSpec.DATE_OF_BIRTH))
				.setGender(Gender.from(i.getInt(BaseUserTableSpec.GENDER)))
				.setAddress(i.getString(BaseUserTableSpec.ADDRESS)).setCity(i.getInt(BaseUserTableSpec.CITY))
				.setTerritory(i.getString(BaseUserTableSpec.TERRITORY))
				.setCountry(i.getString(BaseUserTableSpec.COUNTRY))
				.setFacebookProfile(i.getString(BaseUserTableSpec.FACEBOOK_PROFILE))
				.setTwitterProfile(i.getString(BaseUserTableSpec.TWITTER_PROFILE))
				.setLinkedInProfile(i.getString(BaseUserTableSpec.LINKEDIN_PROFILE))
				.setSkypeProfile(i.getString(BaseUserTableSpec.SKYPE_PROFILE))
				.setPreferredLocale(i.getString(BaseUserTableSpec.PREFERRED_LOCALE));

		return spec;
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.VIEW_OWN_PROFILE)
	public static UserProfileSpec getProfile(Long userId, String... projections) {
		return getProfile(null, userId, projections);
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.VIEW_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static String getRole(Long userId) {
		return get(userId, BaseUserTableSpec.ROLE);
	}

	public static Long getApplicationId(Long userId) {
		return Long.parseLong(get(userId, BaseUserTableSpec.APPLICATION_ID));
	}

	public static String getPreferredLocale(Long userId) {
		return get(userId, BaseUserTableSpec.PREFERRED_LOCALE);
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.MANAGE_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updateEmail(Long principal, Long userId, String email) {

		if (doesEmailExist(email)) {
			throw new PlatformException(UserAccountError.EMAIL_ALREADY_EXISTS);
		}

		Table t = Database.get().getTable(BaseUserTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(userId))
				.addUpdate(S(BaseUserTableSpec.EMAIL).set(email))
				.addUpdate(D(BaseUserTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(userId)))
				.setPredicate(CustomPredicate.UPDATED)
				.setObject(
						ObjectEntity.get(ObjectTypes.EMAIL).setArticle(isMaleUser(userId) ? Article.HIS : Article.HER))
				.withPreposition(Preposition.TO, ClientRBRef.get(email));

		if (principal != null) {
			activity.withPreposition(Preposition.THROUGH,
					SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)));
		}

		ActivityStreamModel.newActivity(activity);
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.MANAGE_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updatePhone(Long principal, Long userId, String phone) {

		if (doesPhoneExist(phone)) {
			throw new PlatformException(UserAccountError.PHONE_ALREADY_EXISTS);
		}

		Table t = Database.get().getTable(BaseUserTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(userId))
				.addUpdate(S(BaseUserTableSpec.PHONE).set(phone))
				.addUpdate(D(BaseUserTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(userId)))
				.setPredicate(CustomPredicate.UPDATED)
				.setObject(ObjectEntity.get(ObjectTypes.PHONE_NUMBER)
						.setArticle(isMaleUser(userId) ? Article.HIS : Article.HER))
				.withPreposition(Preposition.TO, ClientRBRef.get(phone));

		if (principal != null) {
			activity.withPreposition(Preposition.THROUGH,
					SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)));
		}

		ActivityStreamModel.newActivity(activity);
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.MANAGE_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updatePassword(Long principal, Long userId, String currentPassword, String newPassword) {

		Table t = Database.get().getTable(BaseUserTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(userId))
				.withCondition(S(BaseUserTableSpec.PASSWORD).eq(currentPassword))
				.addUpdate(S(BaseUserTableSpec.PASSWORD).set(newPassword))
				.addUpdate(D(BaseUserTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(userId)))
				.setPredicate(CustomPredicate.UPDATED).setObject(ObjectEntity.get(ObjectTypes.PASSWORD)
						.setArticle(isMaleUser(userId) ? Article.HIS : Article.HER));

		if (principal != null) {
			activity.withPreposition(Preposition.THROUGH,
					SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)));
		}

		ActivityStreamModel.newActivity(activity);
	}

	@ModelMethod(functionality = { UserFunctionalities.Constants.MANAGE_OWN_PROFILE,
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updateAvatar(Long principal, Long userId, String blobId) {

		Table t = Database.get().getTable(BaseUserTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(userId))
				.addUpdate(S(BaseUserTableSpec.IMAGE).set(blobId))
				.addUpdate(D(BaseUserTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(userId)))
				.setPredicate(CustomPredicate.UPDATED).setObject(
						ObjectEntity.get(ObjectTypes.IMAGE).setArticle(isMaleUser(userId) ? Article.HIS : Article.HER));

		if (principal != null) {
			activity.withPreposition(Preposition.THROUGH,
					SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)));
		}

		ActivityStreamModel.newActivity(activity);
	}

	@BlockerTodo("Based on user role, consolidate all other entities that belong to this user")
	@ModelMethod(functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public static void updateRole(Long principal, Long userId, String role) {

		Table t = Database.get().getTable(BaseUserTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(BaseUserTableSpec.ID).eq(userId))
				.addUpdate(S(BaseUserTableSpec.ROLE).set(role))
				.addUpdate(D(BaseUserTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)))
				.setPredicate(CustomPredicate.UPDATED).setObject(ObjectEntity.get(ObjectTypes.USER_ROLE))
				.withPreposition(Preposition.OF,
						SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(userId)))
				.withPreposition(Preposition.TO,
						ObjectEntity.get(ObjectTypes.SYSTEM_ROLE).setIdentifiers(FluentArrayList.asList(role)));

		ActivityStreamModel.newActivity(activity);
	}

	protected static void deleteFieldValues(String fieldId) {

		Table t = Database.get().getTable(UserFormValueTable.class);

		DeleteItemSpec spec = new ExpressionSpecBuilder().withCondition(S(UserFormValueTableSpec.FIELD_ID).eq(fieldId))
				.buildForDeleteItem();

		t.deleteItem(spec);
	}

	private static void deleteFieldValuesForUser(Long userId) {

		Table t = Database.get().getTable(UserFormValueTable.class);

		DeleteItemSpec spec = new ExpressionSpecBuilder()
				.withCondition(S(UserFormValueTableSpec.USER_ID).eq(userId.toString())).buildForDeleteItem();

		t.deleteItem(spec);
	}

	@Unexposed
	@ModelMethod(functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public static Map<String, String> getFieldValues(Long userId, Collection<String> fieldIds) {
		Map<String, String> result = new FluentHashMap<>();

		Collection<Item> items = Database.get().getTable(UserFormValueTable.class)
				.getIndex(UserFormValueTableSpec.USER_INDEX)
				.all(QuerySpec.get(UserFormValueTableSpec.USER_ID, userId.toString(), UserFormValueTableSpec.FIELD_ID,
						fieldIds.toArray(new String[fieldIds.size()]), UserFormValueTableSpec.VALUE));

		items.forEach(i -> {
			result.put(i.getString(UserFormValueTableSpec.FIELD_ID), i.getString(UserFormValueTableSpec.VALUE));
		});

		return result;
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_PERSON_NAMES)
	public static Map<Long, String> getPersonNames(List<Long> ids) {

		GetItemsSpec spec = GetItemsSpec.forKeys(
				ids.stream().map(id -> new PrimaryKey(BaseUserTableSpec.ID, id)).collect(Collectors.toList()),
				BaseUserTableSpec.FIRST_NAME, BaseUserTableSpec.MIDDLE_NAME, BaseUserTableSpec.LAST_NAME);

		Map<Long, String> names = new FluentHashMap<>();

		Database.get().batchGetItem(new BatchGetItemRequest().addRequestItem(UserFormValueTable.class, spec))
				.getResponses(UserFormValueTable.class).forEach(i -> {
				});

		return names;
	}

	@Unexposed
	@ModelMethod(functionality = UserFunctionalities.Constants.GET_PERSON_NAMES)
	public static Object getPersonName(Long id, boolean full) {

		if (id.equals(-1l)) {
			return ClientRBRef.get("guest");
		}

		// Todo: Why fetch BaseUserTableSpec.MIDDLE_NAME if !full
		Item i = get(id, BaseUserTableSpec.FIRST_NAME, BaseUserTableSpec.MIDDLE_NAME, BaseUserTableSpec.LAST_NAME);
		return getPersonName(i, full);
	}

	public static String getPersonName(Item i, Boolean full) {

		String fname = i.getString(BaseUserTableSpec.FIRST_NAME);
		String mname = i.getString(BaseUserTableSpec.MIDDLE_NAME);
		String lname = i.getString(BaseUserTableSpec.LAST_NAME);

		return fname + (full ? ClientResources.HtmlCharacterEntities.SPACE + mname : "")
				+ ClientResources.HtmlCharacterEntities.SPACE + lname;
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
	public void unInstall() {
		// TODO Auto-generated method stub

	}

}
