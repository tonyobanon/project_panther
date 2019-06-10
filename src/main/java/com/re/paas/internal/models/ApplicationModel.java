package com.re.paas.internal.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.D;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.annotations.develop.Todo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.IndexedNameSpec;
import com.re.paas.api.classes.KeyValuePair;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.forms.SizeSpec;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.UpdateItemSpec;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.WriteRequest;
import com.re.paas.api.listable.IndexedNameType;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.Model;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.ApplicationDeclineReason;
import com.re.paas.api.models.classes.Gender;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.api.sentences.Article;
import com.re.paas.api.sentences.CustomPredicate;
import com.re.paas.api.sentences.ObjectEntity;
import com.re.paas.api.sentences.Sentence;
import com.re.paas.api.sentences.SubjectEntity;
import com.re.paas.api.sentences.SubordinatingConjuction;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.Application;
import com.re.paas.internal.classes.ApplicationStatus;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.documents.pdf.gen.PDFForm;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.locations.LocationModel;
import com.re.paas.internal.models.helpers.FormFactory;
import com.re.paas.internal.models.helpers.FormFieldRepository;
import com.re.paas.internal.models.helpers.FormFieldRepository.FormField;
import com.re.paas.internal.models.listables.IndexedNameTypes;
import com.re.paas.internal.sentences.SubjectTypes;
import com.re.paas.internal.tables.defs.users.ApplicationFormValueTable;
import com.re.paas.internal.tables.defs.users.ApplicationTable;
import com.re.paas.internal.tables.defs.users.DeclinedApplicationTable;
import com.re.paas.internal.tables.spec.users.ApplicationFormValueTableSpec;
import com.re.paas.internal.tables.spec.users.ApplicationTableSpec;
import com.re.paas.internal.tables.spec.users.DeclinedApplicationTableSpec;
import com.re.paas.internal.utils.FrontendObjectMarshaller;

@Model(dependencies = { ConfigModel.class, FormModel.class })
public class ApplicationModel extends BaseModel {

	@Override
	public String path() {
		return "core/application";
	}

	@Override
	@BlockerTodo("I need to write a new FontProvider to use the cache Service to cache fonts")
	public void preInstall() {

		Logger.get().debug("Generating application questionnaires for all role realms");

		boolean b = false;

		if (b) {

			AbstractRealmDelegate delegate = Realm.getDelegate();

			for (String realmName : delegate.getRealmNames()) {

				Realm realm = delegate.getRealm(realmName);
				generatePDFQuestionnaire(realm);
			}
		}
	}

	@Override
	public void install(InstallOptions options) {

	}

	private static IndexedNameType getIndexedNameType(String role) {
		Realm realm = RoleModel.getRealm(role);
		return realm.applicationSpec().getIndexedNameType();
	}

	@Todo("Add to activity stream")
	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.CREATE_APPLICATION)
	public static Long newApplication(String role) {

		Table t = Database.get().getTable(ApplicationTable.class);

		Item i = new Item().with(ApplicationTableSpec.ROLE, role)
				.with(ApplicationTableSpec.STATUS, ApplicationStatus.CREATED.getValue())
				.with(ApplicationTableSpec.DATE_CREATED, Dates.now())
				.with(ApplicationTableSpec.DATE_UPDATED, Dates.now());

		// Create application with status of CREATED

		Long applicationId = t.putItem(PutItemSpec.forItem(i)).getItem().getLong(ApplicationTableSpec.ID);

		// Update cached list index

		SearchModel.addCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.CREATED.getValue())),
				applicationId);

		return applicationId;
	}

	@BlockerTodo("This function is too expensive. It updates all values every time. Find usages and fix ASAP")
	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static void updateApplication(Long applicationId, Map<String, String> values) {

		ApplicationStatus status = getApplicationStatus(applicationId);

		String role = getApplicationRole(applicationId);
		Realm realm = RoleModel.getRealm(role);
		RealmApplicationSpec applicationSpec = realm.applicationSpec();

		if (!(status.equals(ApplicationStatus.CREATED) || status.equals(ApplicationStatus.OPEN))) {
			throw new ResourceException(ResourceException.UPDATE_NOT_ALLOWED,
					"Application cannot be updated. It may have already been submitted");
		}

		validateApplicationValues(applicationId, realm, values);

		// Delete old values
		deleteFieldValuesForApplication(applicationId);

		Table t = Database.get().getTable(ApplicationFormValueTable.class);

		Item i = new Item().with(ApplicationFormValueTableSpec.APPLICATION_ID, applicationId)
				.with(ApplicationFormValueTableSpec.FIELD_ID,
						FormFieldRepository.getFieldId(realm, FormField.PREFERRED_LOCALE))
				.with(ApplicationFormValueTableSpec.VALUE, LocaleModel.getUserLocale())
				.with(ApplicationFormValueTableSpec.DATE_UPDATED, Dates.now());

		t.putItem(PutItemSpec.forItem(i));

		BatchWriteItemRequest entries = new BatchWriteItemRequest();

		// Save values
		values.forEach((k, v) -> {

			Item e = new Item().with(ApplicationFormValueTableSpec.APPLICATION_ID, applicationId)
					.with(ApplicationFormValueTableSpec.FIELD_ID, k)
					.with(ApplicationFormValueTableSpec.VALUE, v.toString())
					.with(ApplicationFormValueTableSpec.DATE_UPDATED, Dates.now());

			entries.addRequestItem(ApplicationFormValueTableSpec.class, new WriteRequest(PutItemSpec.forItem(e)));
		});

		Database.get().batchWriteItem(entries);

		// Update Application ref, if necessary
		String refField = applicationSpec.getListingRefField();
		String refValue = null;

		String currentRefValue = getApplicationRef(applicationId);

		if (refField != null) {
			refValue = values.get(refField);
			updateApplicationRef(applicationId, refValue);
		}

		if (status.equals(ApplicationStatus.CREATED)) {

			// Update status
			updateApplicationStatus(applicationId, ApplicationStatus.OPEN);

			// Update cached list index

			SearchModel.removeCachedListKey(getIndexedNameType(role),
					FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.CREATED.getValue())),
					applicationId);

			SearchModel.addCachedListKey(getIndexedNameType(role),
					FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.OPEN.getValue())),
					applicationId);

			if (refValue != null) {
				SearchModel.addCachedListKey(getIndexedNameType(role),
						new FluentArrayList<ListingFilter>()
								.with(new ListingFilter("status", ApplicationStatus.OPEN.getValue()))
								.with(new ListingFilter("ref", refValue)),
						applicationId);
			}

		} else if (status.equals(ApplicationStatus.OPEN)) {

			// Update CachedList index, if the ref field was updated

			if (refValue != null) {

				if (currentRefValue != null) {
					SearchModel.removeCachedListKey(getIndexedNameType(role),
							new FluentArrayList<ListingFilter>()
									.with(new ListingFilter("status", ApplicationStatus.OPEN.getValue()))
									.with(new ListingFilter("ref", currentRefValue)),
							applicationId);
				}

				SearchModel.addCachedListKey(getIndexedNameType(role),
						new FluentArrayList<ListingFilter>()
								.with(new ListingFilter("status", ApplicationStatus.OPEN.getValue()))
								.with(new ListingFilter("ref", refValue)),
						applicationId);
			}
		}

		if (applicationSpec.isAutoAccept()) {
			submitApplication(applicationId);
		} else {

			// Add to activity stream

			Sentence activity = Sentence.newInstance().setSubject(getNameSpec(applicationId, realm))
					.setPredicate(CustomPredicate.UPDATED)
					.setObject(ObjectEntity.get(applicationSpec.getBaseObjectType())
							.setIdentifiers(FluentArrayList.asList(applicationId))
							.setArticle(isMaleApplicant(applicationId, realm) ? Article.HIS : Article.HER));

			ActivityStreamModel.newActivity(activity);
		}
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.SUBMIT_APPLICATION)
	@BlockerTodo("Fix validateApplicationValues(..)")
	public static Long submitApplication(Long applicationId) {

		if (!getApplicationStatus(applicationId).equals(ApplicationStatus.OPEN)) {
			throw new ResourceException(ResourceException.UPDATE_NOT_ALLOWED);
		}

		String role = getApplicationRole(applicationId);
		Realm realm = RoleModel.getRealm(role);

		Map<String, String> values = getFieldValues(applicationId);

		// validateApplicationValues(applicationId, values);

		// Update status
		updateApplicationStatus(applicationId, ApplicationStatus.PENDING);

		String refValue = getApplicationRef(applicationId);

		// Update cached list index

		SearchModel.removeCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.OPEN.getValue())), applicationId);

		if (refValue != null) {
			SearchModel.removeCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.OPEN.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		SearchModel.addCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.PENDING.getValue())),
				applicationId);

		if (refValue != null) {
			SearchModel.addCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.PENDING.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		// Add to search index

		IndexedNameSpec nameSpec = getNameSpec(applicationId, realm, values);
		SearchModel.addIndexedName(nameSpec, getIndexedNameType(role));

		// Add to activity stream

		Sentence activity = Sentence.newInstance().setSubject(getNameSpec(applicationId, realm))
				.setPredicate(CustomPredicate.SUBMITTED)
				.setObject(ObjectEntity.get(realm.applicationSpec().getBaseObjectType())
						.setIdentifiers(FluentArrayList.asList(applicationId))
						.setArticle(isMaleApplicant(applicationId, realm) ? Article.HIS : Article.HER));

		ActivityStreamModel.newActivity(activity);

		return applicationId;
	}

	@BlockerTodo("Add support for server-side validation, since we know the field types")
	private static void validateApplicationValues(Long applicationId, Realm realm, Map<String, String> values) {

		// Scan for relevant fields, verify that proper values are provided for them

		for (Entry<String, Boolean> e : FormModel.listAllFieldKeys(FormSectionType.APPLICATION_FORM, realm)
				.entrySet()) {

			if (!e.getValue()) {
				continue;
			}

			String value = values.get(e.getKey());
			if (value == null || value.trim().isEmpty()) {
				throw new ResourceException(ResourceException.FAILED_VALIDATION, e.getKey());
			}
		}
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.DOWNLOAD_QUESTIONNAIRE)
	public static InputStream getPDFQuestionnaire(String role) {
		Realm realm = RoleModel.getRealm(role);
		try {
			InputStream in = Files.newInputStream(getFormPath(realm));
			return in;
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	private static void generatePDFQuestionnaire(Realm realm) {

		String orgenizationName = ConfigModel.get(ConfigKeys.ORGANIZATION_NAME);

		PDFForm form = new PDFForm().setLogoURL(ConfigModel.get(ConfigKeys.ORGANIZATION_LOGO_URL))
				.setSubtitleLeft(orgenizationName)
				.setTitle(Utils.prettify(realm.name().toLowerCase() + "  e-Registration"))
				.setSubtitleRight(Application.SOFTWARE_VENDOR_EMAIL);

		FormModel.listSections(FormSectionType.APPLICATION_FORM, realm).forEach((section) -> {

			section.withFields(FormModel.getFields(FormSectionType.APPLICATION_FORM, section.getId()));

			form.withSection(section);
		});

		// Generate using default locale

		FormFactory.toPDF(LocaleModel.defaultLocale(), new SizeSpec(4), new SizeSpec(5), new SizeSpec(3),
				form, getFormPath(realm));

		Logger.get().debug("Saving questionairre form for " + realm.name());
	}
	
	private static Path getFormPath(Realm realm) {
		return Paths.get("/forms/application", "/" + realm.name() + ".pdf");
	}
	

	public static Map<String, String> getFieldValues(Long applicationId, Collection<String> fieldIds) {

		Map<String, String> result = new FluentHashMap<>();

		Collection<Item> items = Database.get().getTable(ApplicationFormValueTable.class)
				.getIndex(ApplicationFormValueTableSpec.APPLICATION_INDEX)
				.all(ApplicationFormValueTableSpec.APPLICATION_ID, applicationId.toString(),
						ApplicationFormValueTableSpec.FIELD_ID, fieldIds.toArray(new String[fieldIds.size()]),
						ApplicationFormValueTableSpec.VALUE);

		items.forEach(i -> {
			result.put(i.getString(ApplicationFormValueTableSpec.FIELD_ID),
					i.getString(ApplicationFormValueTableSpec.VALUE));
		});

		return result;
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM)
	public static String getApplicationRole(Long id) {
		Item i = Database.get().getTable(ApplicationTable.class)
				.getItem(GetItemSpec.forKey(ApplicationTableSpec.ID, id, ApplicationTableSpec.ROLE));
		return i.getString(ApplicationTableSpec.ROLE);
	}

	private static String getApplicationRef(Long id) {
		Item i = Database.get().getTable(ApplicationTable.class)
				.getItem(GetItemSpec.forKey(ApplicationTableSpec.ID, id, ApplicationTableSpec.REF));
		return i.getString(ApplicationTableSpec.REF);
	}

	private static ApplicationStatus getApplicationStatus(Long id) {
		Item i = Database.get().getTable(ApplicationTable.class)
				.getItem(GetItemSpec.forKey(ApplicationTableSpec.ID, id, ApplicationTableSpec.STATUS));
		return ApplicationStatus.from(i.getInt(ApplicationTableSpec.STATUS));
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static Map<String, String> getFieldValues(Long applicationId) {
		return getFieldValues(applicationId, false);
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.UPDATE_APPLICATION)
	public static Map<String, String> getFieldValues(Long applicationId, Boolean consolidate) {

		Realm realm = RoleModel.getRealm(getApplicationRole(applicationId));

		Collection<String> fieldIds = FormModel.listAllFieldKeys(FormSectionType.APPLICATION_FORM, realm).keySet();

		Map<String, String> result = getFieldValues(applicationId, fieldIds);

		if (consolidate) {

			Map<FormField, String> fieldTypes = FormFieldRepository.getFieldIds(realm);
			Map<String, FormField> invertedFieldTypes = new HashMap<>(fieldTypes.size());

			fieldTypes.forEach((k, v) -> {
				invertedFieldTypes.put(v, k);
			});

			result.entrySet().stream().map(e -> {
				FormField fieldType = invertedFieldTypes.get(e.getKey());
				return e != null ? new KeyValuePair<>(e.getKey(), getConsolidatedFieldValue(fieldType, e.getValue()))
						: e;
			});
		}

		return result;
	}

	private static String getConsolidatedFieldValue(FormField type, String value) {

		switch (type) {

		case CITY:
		case ORGANIZATION_CITY:
			return LocationModel.getCityName(Integer.parseInt(value));
		case COUNTRY:
		case ORGANIZATION_COUNTRY:
			return LocationModel.getCountryName(value);
		case GENDER:
			return Utils.prettify(Gender.from(Integer.parseInt(value)).name());
		case STATE:
		case ORGANIZATION_STATE:
			return LocationModel.getTerritoryName(value);

		case FACEBOOK_PROFILE:
			return "https://www.facebook.com/" + value;

		case TWITTER_PROFILE:
			return "https://www.twitter.com/" + value;

		case LINKEDIN_PROFILE:
			return "https://www.linkedin.com/" + value;

		case SKYPE_PROFILE:
			return "https://www.skype.com/" + value;
		default:
			return value;
		}
	}

	protected static void deleteFieldValues(String fieldId) {

		Table t = Database.get().getTable(ApplicationFormValueTable.class);

		DeleteItemSpec spec = new ExpressionSpecBuilder()
				.withCondition(S(ApplicationFormValueTableSpec.FIELD_ID).eq(fieldId)).buildForDeleteItem();

		t.deleteItem(spec);
	}

	private static void deleteFieldValuesForApplication(Long applicationId) {

		Table t = Database.get().getTable(ApplicationFormValueTable.class);

		DeleteItemSpec spec = new ExpressionSpecBuilder()
				.withCondition(S(ApplicationFormValueTableSpec.APPLICATION_ID).eq(applicationId.toString()))
				.buildForDeleteItem();

		t.deleteItem(spec);
	}

	private static void updateApplicationRef(Long applicationId, String ref) {

		if (ref == null) {
			return;
		}

		Table t = Database.get().getTable(ApplicationTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(ApplicationTableSpec.ID).eq(applicationId))
				.addUpdate(S(ApplicationTableSpec.REF).set(ref))
				.addUpdate(D(ApplicationTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);
	}

	private static void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
		Table t = Database.get().getTable(ApplicationTable.class);

		UpdateItemSpec spec = new ExpressionSpecBuilder().withCondition(N(ApplicationTableSpec.ID).eq(applicationId))
				.addUpdate(N(ApplicationTableSpec.STATUS).set(status.getValue()))
				.addUpdate(D(ApplicationTableSpec.DATE_UPDATED).set(Dates.now())).buildForUpdate();

		t.updateItem(spec);
	}

	@ModelMethod(functionality = {})
	public static Long acceptApplication(Long principal, Long applicationId) {

		String role = getApplicationRole(applicationId);
		Realm realm = RoleModel.getRealm(role);

		Map<FormField, String> keys = FormFieldRepository.getFieldIds(realm);
		Map<String, String> values = getFieldValues(applicationId);

		UserProfileSpec user = getConsolidatedUser(keys, values).setApplicationId(applicationId);
		Long userId = BaseUserModel.registerUser(user, role, principal);

		// Call realm hook, before updating status in case the realm wants to
		// update the application
		BiConsumer<Long, Long> hook = realm.applicationSpec().getOnAccept();
		if (hook != null) {
			hook.accept(applicationId, userId);
		}

		// Update application status
		updateApplicationStatus(applicationId, ApplicationStatus.ACCEPTED);

		String refValue = getApplicationRef(applicationId);

		// Update cached list index

		SearchModel.removeCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.PENDING.getValue())),
				applicationId);

		if (refValue != null) {
			SearchModel.removeCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.PENDING.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		SearchModel.addCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.ACCEPTED.getValue())),
				applicationId);

		if (refValue != null) {
			SearchModel.addCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.ACCEPTED.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		// Update type and entity Id of indexed name

		SearchModel.updateIndexedNameType(applicationId, userId, getIndexedNameType(role), IndexedNameTypes.USER);

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)))
				.setPredicate(CustomPredicate.APPROVED)
				.setObject(ObjectEntity.get(realm.applicationSpec().getBaseObjectType())
						.setIdentifiers(FluentArrayList.asList(applicationId)));

		ActivityStreamModel.newActivity(activity);

		return userId;
	}

	@ModelMethod(functionality = {})
	public static Map<Integer, Object> getApplicationDeclineReasons() {
		Map<Integer, Object> reasons = new HashMap<>();
		for (ApplicationDeclineReason reason : ApplicationDeclineReason.values()) {
			reasons.put(reason.getValue(), ClientRBRef.get(reason));
		}
		return reasons;
	}

	@ModelMethod(functionality = {})
	public static void declineApplication(Long applicationId, Long principal, ApplicationDeclineReason reason) {

		// Update application status
		updateApplicationStatus(applicationId, ApplicationStatus.DECLINED);

		// Create new declined application

		Table t = Database.get().getTable(DeclinedApplicationTable.class);

		Item i = new Item().with(DeclinedApplicationTableSpec.APPLICATION_ID, applicationId)
				.with(DeclinedApplicationTableSpec.STAFF_ID, principal)
				.with(DeclinedApplicationTableSpec.REASON, reason.getValue())
				.with(DeclinedApplicationTableSpec.DATE_CREATED, Dates.now());

		t.putItem(PutItemSpec.forItem(i));

		String role = getApplicationRole(applicationId);
		Realm realm = RoleModel.getRealm(role);

		String refValue = getApplicationRef(applicationId);

		// Update cached list index

		SearchModel.removeCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.PENDING.getValue())),
				applicationId);

		if (refValue != null) {
			SearchModel.removeCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.PENDING.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		SearchModel.addCachedListKey(getIndexedNameType(role),
				FluentArrayList.asList(new ListingFilter("status", ApplicationStatus.DECLINED.getValue())),
				applicationId);

		if (refValue != null) {
			SearchModel.addCachedListKey(getIndexedNameType(role),
					new FluentArrayList<ListingFilter>()
							.with(new ListingFilter("status", ApplicationStatus.DECLINED.getValue()))
							.with(new ListingFilter("ref", refValue)),
					applicationId);
		}

		// remove indexed name

		SearchModel.removeIndexedName(applicationId.toString(), getIndexedNameType(role));

		// Add to activity stream

		Sentence activity = Sentence.newInstance()
				.setSubject(SubjectEntity.get(SubjectTypes.USER).setIdentifiers(FluentArrayList.asList(principal)))
				.setPredicate(CustomPredicate.DECLINED)
				.setObject(ObjectEntity.get(realm.applicationSpec().getBaseObjectType())
						.setIdentifiers(FluentArrayList.asList(applicationId)))
				.setSubordinativeClause(SubordinatingConjuction.BECAUSE, ClientRBRef.get(reason));

		ActivityStreamModel.newActivity(activity);

		// Call realm hook
		BiConsumer<Long, ApplicationDeclineReason> hook = realm.applicationSpec().getOnDecline();
		if (hook != null) {
			hook.accept(applicationId, reason);
		}
	}

	private static UserProfileSpec getConsolidatedUser(Map<FormField, String> keys, Map<String, String> values) {

		return new UserProfileSpec()

				.setFirstName(values.get(keys.get(FormField.FIRST_NAME)))
				.setLastName(values.get(keys.get(FormField.LAST_NAME))).setImage(values.get(keys.get(FormField.IMAGE)))

				.setMiddleName(values.get(keys.get(FormField.MIDDLE_NAME)))
				.setDateOfBirth(FrontendObjectMarshaller.unmarshalDate(values.get(keys.get(FormField.DATE_OF_BIRTH))))
				.setGender(Gender.from(Integer.parseInt(values.get(keys.get(FormField.GENDER)))))
				.setEmail(values.get(keys.get(FormField.EMAIL)).toString())

				.setAddress(values.get(keys.get(FormField.ADDRESS)))
				.setPhone(values.get(keys.get(FormField.PHONE_NUMBER)))
				.setCity(Integer.parseInt(values.get(keys.get(FormField.CITY))))
				.setTerritory(values.get(keys.get(FormField.STATE))).setCountry(values.get(keys.get(FormField.COUNTRY)))

				.setFacebookProfile(values.get(keys.get(FormField.FACEBOOK_PROFILE)))
				.setTwitterProfile(values.get(keys.get(FormField.TWITTER_PROFILE)))
				.setLinkedInProfile(values.get(keys.get(FormField.LINKEDIN_PROFILE)))
				.setSkypeProfile(values.get(keys.get(FormField.SKYPE_PROFILE)))

				.setPreferredLocale(values.get(keys.get(FormField.PREFERRED_LOCALE)));
	}

//	private static String getApplicantAvatar(Long applicationId, Realm realm, Map<String, String> fieldValues) {
//
//		String imageField = FormFieldRepository.getFieldId(realm, FormFieldRepository.FormField.IMAGE);
//
//		if (fieldValues == null) {
//			fieldValues = getFieldValues(applicationId, new FluentArrayList<String>().with(imageField));
//		}
//
//		return fieldValues.get(imageField);
//	}

	private static Boolean isMaleApplicant(Long applicationId, Realm realm) {
		return isMaleApplicant(applicationId, realm, null);
	}

	private static Boolean isMaleApplicant(Long applicationId, Realm realm, Map<String, String> fieldValues) {

		String genderField = FormFieldRepository.getFieldId(realm, FormFieldRepository.FormField.GENDER);

		if (fieldValues == null) {
			fieldValues = getFieldValues(applicationId, new FluentArrayList<String>().with(genderField));
		}

		Integer value = Integer.parseInt(fieldValues.get(genderField));
		Gender gender = Gender.from(value);

		return gender.equals(Gender.MALE);
	}

	public static IndexedNameSpec getNameSpec(Long applicationId, Realm realm) {
		return getNameSpec(applicationId, realm, null);
	}

	private static IndexedNameSpec getNameSpec(Long applicationId, Realm realm, Map<String, String> fieldValues) {

		IndexedNameSpec spec = new IndexedNameSpec();

		String firstNameField = FormFieldRepository.getFieldId(realm, FormFieldRepository.FormField.FIRST_NAME);
		String middleNameField = FormFieldRepository.getFieldId(realm, FormFieldRepository.FormField.MIDDLE_NAME);
		String lastNameField = FormFieldRepository.getFieldId(realm, FormFieldRepository.FormField.LAST_NAME);

		if (fieldValues == null) {
			fieldValues = getFieldValues(applicationId,
					new FluentArrayList<String>().with(firstNameField).with(middleNameField).with(lastNameField));
		}

		spec.setKey(applicationId.toString()).setX(fieldValues.get(firstNameField)).setY(fieldValues.get(lastNameField))
				.setZ(fieldValues.get(middleNameField));

		return spec;
	}

	@Override
	public void start() {

	}

	@Override
	public void update() {

	}

	@Override
	public void unInstall() {

	}

}
