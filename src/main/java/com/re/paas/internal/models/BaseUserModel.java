package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Note;
import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.ResourceException;
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
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.core.keys.MetricKeys;
import com.re.paas.internal.fusion.functionalities.AuthFunctionalities;
import com.re.paas.internal.fusion.functionalities.UserFunctionalities;
import com.re.paas.internal.fusion.services.impl.Unexposed;
import com.re.paas.internal.models.errors.UserAccountError;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.listables.IndexedNameTypes;
import com.re.paas.internal.models.tables.users.BaseUserEntity;
import com.re.paas.internal.models.tables.users.UserFormValueEntity;
import com.re.paas.internal.realms.AdminRealm;
import com.re.paas.internal.sentences.ObjectTypes;
import com.re.paas.internal.sentences.SubjectTypes;

@Todo("stop storing passwords as plain text, hash it instead")
@Model(dependencies = RoleModel.class)
public class BaseUserModel extends BaseModel {

	@Override
	public String path() {
		return "core/base-user";
	}

	@Override
	public void preInstall() {

		ConfigModel.put(ConfigKeys.USER_COUNT_ARCHIVE, 0);
		ConfigModel.put(ConfigKeys.USER_COUNT_CURRENT, 0);
	}

	@Override
	public void install(InstallOptions options) {

		for (UserProfileSpec spec : options.getAdmins()) {
			registerUser(spec, RoleModel.getDefaultRole(new AdminRealm()), -1l);
		}
	}

	private static Long nextKey() {
		Long current = Long.parseLong(ConfigModel.get(ConfigKeys.USER_COUNT_ARCHIVE));
		Long next = current + 1;
		ConfigModel.put(ConfigKeys.USER_COUNT_ARCHIVE, next);
		return next;
	}

	protected static void deleteUser(Long id) {

		// Delete form values
		deleteFieldValuesForUser(id);

		// Delete entity
		ofy().delete().key(Key.create(BaseUserEntity.class, id)).now();

		ConfigModel.put(ConfigKeys.USER_COUNT_CURRENT,
				Integer.parseInt(ConfigModel.get(ConfigKeys.USER_COUNT_CURRENT)) - 1);

		// Update cached list index
		SearchModel.removeCachedListKey(IndexedNameTypes.USER, id);
	}

	public static Long getUserId(String email) {
		BaseUserEntity e = ofy().load().type(BaseUserEntity.class).filter("email = ", email).first().now();
		if (e != null) {
			return e.getId();
		} else {
			throw new NullPointerException();
		}
	}

	@ModelMethod(functionality = AuthFunctionalities.Constants.EMAIL_LOGIN_USER)
	public static Long loginByEmail(String email, String password) {

		if (!doesEmailExist(email)) {
			// Incorrect email
			throw new PlatformException(UserAccountError.EMAIL_DOES_NOT_EXIST);
		}

		BaseUserEntity e = ofy().load().type(BaseUserEntity.class).filter("email = ", email).first().now();
		if (e.getPassword().equals(password)) {
			return e.getId();
		} else {
			// Wrong password
			throw new PlatformException(UserAccountError.INCORRECT_PASSWORD);
		}
	}

	@Note("The phone index has been commented out in the entity")
	@BlockerTodo("Phone Index is currently not used on the frontend. Indexes are expensive remember")
	@ModelMethod(functionality = AuthFunctionalities.Constants.PHONE_LOGIN_USER)
	public static Long loginByPhone(Long phone, String password) {

		if (!doesPhoneExist(phone)) {
			// Incorrect phone
			throw new PlatformException(UserAccountError.PHONE_DOES_NOT_EXIST);
		}

		BaseUserEntity e = ofy().load().type(BaseUserEntity.class).filter("phone = ", phone.toString()).first().now();
		if (e.getPassword().equals(password)) {
			return e.getId();
		} else {
			// Wrong password
			throw new PlatformException(UserAccountError.INCORRECT_PASSWORD);
		}
	}

	@Todo("Validate user's phone number, and other info properly, make method protected")
	public static Long registerUser(UserProfileSpec spec, String role, Long principal) {

		BaseUserEntity e = EntityHelper.fromObjectModel(role, principal, spec);

		if (doesEmailExist(e.getEmail())) {
			throw new PlatformException(UserAccountError.EMAIL_ALREADY_EXISTS);
		}

		if (doesPhoneExist(e.getPhone())) {
			throw new PlatformException(UserAccountError.PHONE_ALREADY_EXISTS);
		}

		e.setId(nextKey());

		ofy().save().entity(e).now();

		ConfigModel.put(ConfigKeys.USER_COUNT_CURRENT,
				Integer.parseInt(ConfigModel.get(ConfigKeys.USER_COUNT_CURRENT)) + 1);
		MetricsModel.increment(MetricKeys.USERS_COUNT);

		// Update cached list index
		SearchModel.addCachedListKey(IndexedNameTypes.USER, e.getId());

		return e.getId();
	}

	private static boolean doesEmailExist(String email) {
		return ofy().load().type(BaseUserEntity.class).filter("email = ", email).first().now() != null;
	}

	private static boolean doesPhoneExist(Long phone) {
		return ofy().load().type(BaseUserEntity.class).filter("phone = ", phone.toString()).first().now() != null;
	}

	@PlatformInternal
	public static BaseUserEntity get(Long userId) {
		return ofy().load().type(BaseUserEntity.class).id(userId).safe();
	}

	@ModelMethod(functionality = { 
			UserFunctionalities.Constants.VIEW_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static String getAvatar(Long userId) {
		return userId.equals(-1l) ? null : get(userId).getImage();
	}

	protected static Boolean isMaleUser(Long userId) {
		return Gender.from(get(userId).getGender()).equals(Gender.MALE);
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public static boolean canAccessUserProfile(Long principal, Long userId) {

		Realm principalRealm = RoleModel.getRealm(getRole(principal));
		Realm userRealm = RoleModel.getRealm(getRole(userId));

		return principalRealm.authority() >= userRealm.authority();
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_USER_PROFILE)
	public static UserProfileSpec getProfile(Long principal, Long userId) {

		if (!canAccessUserProfile(principal, userId)) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		return EntityHelper.toObjectModel(get(userId));
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.VIEW_OWN_PROFILE)
	public static UserProfileSpec getProfile(Long userId) {
		return EntityHelper.toObjectModel(get(userId));
	}

	@ModelMethod(functionality = { 
			UserFunctionalities.Constants.VIEW_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static String getRole(Long userId) {
		return get(userId).getRole();
	}
	
	public static Long getApplicationId(Long userId) {
		return get(userId).getApplicationId();
	}

	public static String getPreferredLocale(Long userId) {
		return get(userId).getPreferredLocale();
	}

	@ModelMethod(functionality = { 
			UserFunctionalities.Constants.MANAGE_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updateEmail(Long principal, Long userId, String email) {

		if (doesEmailExist(email)) {
			throw new PlatformException(UserAccountError.EMAIL_ALREADY_EXISTS);
		}

		BaseUserEntity e = get(userId).setEmail(email).setDateUpdated(Dates.now());
		;
		ofy().save().entity(e).now();

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

	@ModelMethod(functionality = { 
			UserFunctionalities.Constants.MANAGE_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updatePhone(Long principal, Long userId, Long phone) {

		if (doesPhoneExist(phone)) {
			throw new PlatformException(UserAccountError.PHONE_ALREADY_EXISTS);
		}

		BaseUserEntity e = get(userId).setPhone(phone).setDateUpdated(Dates.now());
		;
		ofy().save().entity(e).now();

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

	@ModelMethod(functionality = { 
			UserFunctionalities.Constants.MANAGE_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS })
	public static void updatePassword(Long principal, Long userId, String currentPassword, String newPassword) {
		BaseUserEntity e = get(userId);

		if (e.getPassword().equals(currentPassword)) {
			e.setPassword(newPassword).setDateUpdated(Dates.now());
		} else {
			throw new PlatformException(UserAccountError.PASSWORDS_MISMATCH);
		}

		ofy().save().entity(e).now();

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

	@ModelMethod(functionality = {
			UserFunctionalities.Constants.MANAGE_OWN_PROFILE, 
			UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS})
	public static void updateAvatar(Long principal, Long userId, String blobId) {
		BaseUserEntity e = get(userId).setImage(blobId).setDateUpdated(Dates.now());
		ofy().save().entity(e).now();

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

		BaseUserEntity e = get(userId).setRole(role).setDateUpdated(Dates.now());
		ofy().save().entity(e).now();

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

		List<Key<UserFormValueEntity>> keys = new FluentArrayList<>();

		ofy().load().type(UserFormValueEntity.class).filter("fieldId = ", fieldId).forEach(e -> {
			keys.add(Key.create(UserFormValueEntity.class, e.getId()));
		});

		ofy().delete().keys(keys).now();
	}

	private static void deleteFieldValuesForUser(Long userId) {

		List<Key<UserFormValueEntity>> keys = new FluentArrayList<>();

		ofy().load().type(UserFormValueEntity.class).filter("userId = ", userId).forEach(e -> {
			keys.add(Key.create(UserFormValueEntity.class, e.getId()));
		});

		ofy().delete().keys(keys).now();
	}

	@Unexposed
	@ModelMethod(functionality = UserFunctionalities.Constants.MANAGE_USER_ACCOUNTS)
	public static Map<Long, String> getFieldValues(Long userId, Collection<Long> fieldIds) {

		Map<Long, String> result = new FluentHashMap<>();

		fieldIds.forEach(fieldId -> {

			UserFormValueEntity e = ofy().load().type(UserFormValueEntity.class).filter("fieldId = ", fieldId)
					.filter("userId = ", userId).first().now();

			result.put(fieldId, e != null ? e.getValue() : null);
		});
		return result;
	}

	@ModelMethod(functionality = UserFunctionalities.Constants.GET_PERSON_NAMES)
	public static Map<Long, String> getPersonNames(List<Long> ids) {
		Map<Long, String> names = new FluentHashMap<>();
		ofy().load().type(BaseUserEntity.class).ids(ids).forEach((k, v) -> {
			names.put(k, v.getFirstName() + " " + v.getMiddleName() + " " + v.getLastName());
		});
		return names;
	}

	@Unexposed
	@ModelMethod(functionality = UserFunctionalities.Constants.GET_PERSON_NAMES)
	public static Object getPersonName(Long id, boolean full) {

		if (id.equals(-1l)) {
			return ClientRBRef.get("guest");
		}
		BaseUserEntity v = ofy().load().type(BaseUserEntity.class).id(id).safe();
		return (full
				? v.getFirstName() + ClientResources.HtmlCharacterEntities.SPACE + v.getMiddleName()
						+ ClientResources.HtmlCharacterEntities.SPACE + v.getLastName()
				: v.getFirstName() + ClientResources.HtmlCharacterEntities.SPACE + v.getLastName());
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
