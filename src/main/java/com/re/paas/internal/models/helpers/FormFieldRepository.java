package com.re.paas.internal.models.helpers;

import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.fusion.server.BaseService;
import com.re.paas.api.fusion.services.AbstractServiceDelegate;
import com.re.paas.api.models.classes.Gender;
import com.re.paas.api.realms.Realm;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.realms.AgentRealm;
import com.re.paas.apps.rex.realms.OrganizationAdminRealm;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.errors.RealmError;
import com.re.paas.internal.fusion.functionalities.LocationFunctionalities;
import com.re.paas.internal.models.ConfigModel;
import com.re.paas.internal.models.FormModel;
import com.re.paas.internal.realms.AdminRealm;

@BlockerTodo("Add proper sort order for form section and questions")
public class FormFieldRepository {

	private static final String FORM_FIELD_MAPPING_KEY_PREFIX = "FORM_FIELD_MAPPING_";

	public static void createDefaultFields() {

		AbstractServiceDelegate serviceDelegate = BaseService.getDelegate();

		FormModel.newSection(ClientRBRef.get("profile_information"), FormSectionType.APPLICATION_FORM)
				.forEach((k, v) -> {

					saveFieldId(k, FormField.FIRST_NAME,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("first_name"))
											.setSortOrder(2).setIsDefault(true)));

					saveFieldId(k, FormField.LAST_NAME,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("last_name"))
											.setSortOrder(4).setIsDefault(true)));

					saveFieldId(k, FormField.EMAIL,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.EMAIL, ClientRBRef.get("email"))
											.setSortOrder(7).setIsDefault(true)));

					saveFieldId(k, FormField.PASSWORD,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.SECRET, ClientRBRef.get("password"))
											.setSortOrder(8).setIsDefault(true)));

					saveFieldId(k, FormField.PHONE_NUMBER,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.PHONE, ClientRBRef.get("phone_number"))
											.setSortOrder(9).setIsDefault(true)));

					saveFieldId(k, FormField.PREFERRED_LOCALE,
							FormModel.newSimpleField(v,
									(SimpleField) new SimpleField(InputType.PREFERED_LOCALE,
											ClientRBRef.get("preferred_locale")).setSortOrder(10).setIsDefault(true)
													.setIsRequired(false).setIsVisible(false)));

					if (k instanceof AdminRealm || k instanceof OrganizationAdminRealm || k instanceof AgentRealm) {

						saveFieldId(k, FormField.IMAGE,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.IMAGE, ClientRBRef.get("passport"))
												.setSortOrder(1).setIsDefault(true).setIsRequired(false)));

						saveFieldId(k, FormField.MIDDLE_NAME,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("middle_name"))
												.setSortOrder(3).setIsDefault(true).setIsRequired(false)));

						saveFieldId(k, FormField.GENDER,
								FormModel.newCompositeField(v,
										(CompositeField) new CompositeField(ClientRBRef.get("gender"))
												.withItem(ClientRBRef.get("male"), Gender.MALE.getValue())
												.withItem(ClientRBRef.get("female"), Gender.FEMALE.getValue())
												.setSortOrder(5).setIsDefault(true)));

						saveFieldId(k, FormField.DATE_OF_BIRTH, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.DATE_OF_BIRTH, ClientRBRef.get("date_of_birth"))
										.setSortOrder(6).setIsDefault(true)));

					}

				});

		FormModel.newSection(ClientRBRef.get("contact_information"), FormSectionType.APPLICATION_FORM)
				.forEach((k, v) -> {

					if (k instanceof AdminRealm || k instanceof OrganizationAdminRealm || k instanceof AgentRealm) {

						saveFieldId(k, FormField.ADDRESS,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.ADDRESS, ClientRBRef.get("address"))
												.setSortOrder(2).setIsDefault(true)));

						String countryField = FormModel.newCompositeField(v,
								(CompositeField) new CompositeField(ClientRBRef.get("country")).setItemsSource(
										serviceDelegate.getFunctionalityService(LocationFunctionalities.GET_COUNTRY_NAMES)
												.get(0))
										.setSortOrder(3).setIsDefault(true));
						saveFieldId(k, FormField.COUNTRY, countryField);

						String stateField = FormModel
								.newCompositeField(v,
										(CompositeField) new CompositeField(ClientRBRef.get("state"))
												.setItemsSource(
														serviceDelegate
																.getFunctionalityService(
																		LocationFunctionalities.GET_TERRITORY_NAMES)
																.get(0))
												.setContext(countryField).setSortOrder(4).setIsDefault(true));
						saveFieldId(k, FormField.STATE, stateField);

						String cityField = FormModel.newCompositeField(v,
								(CompositeField) new CompositeField(ClientRBRef.get("city"))
										.setItemsSource(serviceDelegate
												.getFunctionalityService(LocationFunctionalities.GET_CITY_NAMES).get(0))
										.setContext(stateField).setSortOrder(5).setIsDefault(true));
						saveFieldId(k, FormField.CITY, cityField);

						saveFieldId(k, FormField.FACEBOOK_PROFILE, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("facebook_profile"))
										.setSortOrder(6).setIsDefault(true).setIsRequired(false)));

						saveFieldId(k, FormField.TWITTER_PROFILE, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("twitter_profile"))
										.setSortOrder(7).setIsDefault(true).setIsRequired(false)));

						saveFieldId(k, FormField.LINKEDIN_PROFILE, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("linkedin_profile"))
										.setSortOrder(8).setIsDefault(true).setIsRequired(false)));

						saveFieldId(k, FormField.SKYPE_PROFILE,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("skype_profile"))
												.setSortOrder(9).setIsDefault(true).setIsRequired(false)));

					}

				});

		FormModel.newSection(ClientRBRef.get("organization_profile"), FormSectionType.APPLICATION_FORM)
				.forEach((k, v) -> {

					if (k instanceof OrganizationAdminRealm) {

						// This is hidden, and is populated in OrganizationAdminRealm after the
						// admin profile is created
						saveFieldId(k, FormField.ORGANIZATION_ID, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("organization_id"))
										.setSortOrder(1).setIsDefault(true).setIsVisible(false)));

						saveFieldId(k, FormField.ORGANIZATION_NAME, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("organization_name"))
										.setSortOrder(2).setIsDefault(true)));

						saveFieldId(k, FormField.ORGANIZATION_EMAIL, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.EMAIL, ClientRBRef.get("organization_email"))
										.setSortOrder(4).setIsDefault(true)));

						saveFieldId(k, FormField.ORGANIZATION_PHONE, FormModel.newSimpleField(v,
								(SimpleField) new SimpleField(InputType.PHONE, ClientRBRef.get("organization_phone"))
										.setSortOrder(5).setIsDefault(true)));

						saveFieldId(k, FormField.ORGANIZATION_LOGO,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.IMAGE, ClientRBRef.get("logo"))
												.setSortOrder(6).setIsDefault(true)));

						saveFieldId(k, FormField.ORGANIZATION_ADDRESS,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.ADDRESS, ClientRBRef.get("address"))
												.setSortOrder(8).setIsDefault(true)));

						saveFieldId(k, FormField.ORGANIZATION_POSTAL_CODE,
								FormModel.newSimpleField(v,
										(SimpleField) new SimpleField(InputType.PLAIN, ClientRBRef.get("postal_code"))
												.setSortOrder(8).setIsDefault(true)));

						String countryField = FormModel.newCompositeField(v,
								(CompositeField) new CompositeField(ClientRBRef.get("country")).setItemsSource(
										serviceDelegate.getFunctionalityService(LocationFunctionalities.GET_COUNTRY_NAMES)
												.get(0))
										.setSortOrder(10).setIsDefault(true));
						saveFieldId(k, FormField.ORGANIZATION_COUNTRY, countryField);

						String stateField = FormModel
								.newCompositeField(v,
										(CompositeField) new CompositeField(ClientRBRef.get("state"))
												.setItemsSource(
														serviceDelegate
																.getFunctionalityService(
																		LocationFunctionalities.GET_TERRITORY_NAMES)
																.get(0))
												.setContext(countryField).setSortOrder(12).setIsDefault(true));
						saveFieldId(k, FormField.ORGANIZATION_STATE, stateField);

						String cityField = FormModel.newCompositeField(v,
								(CompositeField) new CompositeField(ClientRBRef.get("city"))
										.setItemsSource(serviceDelegate
												.getFunctionalityService(LocationFunctionalities.GET_CITY_NAMES).get(0))
										.setContext(stateField).setSortOrder(14).setIsDefault(true));
						saveFieldId(k, FormField.ORGANIZATION_CITY, cityField);

					}

				});

		FormModel.newSection("agent_profile", FormSectionType.APPLICATION_FORM).forEach((k, v) -> {

			if (k instanceof AgentRealm) {

				String cityField = getFieldId(k, FormField.CITY);

				String organizationField = FormModel
						.newCompositeField(v,
								(CompositeField) new CompositeField(ClientRBRef.get("organization"))
										.setItemsSource(serviceDelegate
												.getFunctionalityService(
														AgentOrganizationFunctionalities.LIST_AGENT_ORGANIZATION_NAMES)
												.get(0))
										.setContext(cityField).setSortOrder(2).setIsDefault(true));

				saveFieldId(k, FormField.ORGANIZATION_ID, organizationField);

				saveFieldId(k, FormField.YEARS_OF_EXPERIENCE, FormModel.newSimpleField(v,
						(SimpleField) new SimpleField(InputType.NUMBER_2L, ClientRBRef.get("years_of_experience"))
								.setSortOrder(4).setIsDefault(true)));

			}

		});

	}

	public static Map<FormField, String> getFieldIds(Realm realm) {

		FluentHashMap<FormField, String> result = new FluentHashMap<FormField, String>()

				.with(FormField.FIRST_NAME, FormFieldRepository.getFieldId(realm, FormField.FIRST_NAME))
				.with(FormField.LAST_NAME, FormFieldRepository.getFieldId(realm, FormField.LAST_NAME))
				.with(FormField.EMAIL, FormFieldRepository.getFieldId(realm, FormField.EMAIL))
				.with(FormField.PASSWORD, FormFieldRepository.getFieldId(realm, FormField.PASSWORD))
				.with(FormField.PHONE_NUMBER, FormFieldRepository.getFieldId(realm, FormField.PHONE_NUMBER))
				.with(FormField.PREFERRED_LOCALE, FormFieldRepository.getFieldId(realm, FormField.PREFERRED_LOCALE));

		if (realm instanceof AdminRealm || realm instanceof OrganizationAdminRealm || realm instanceof AgentRealm) {

			result

					.with(FormField.IMAGE, FormFieldRepository.getFieldId(realm, FormField.IMAGE))
					.with(FormField.MIDDLE_NAME, FormFieldRepository.getFieldId(realm, FormField.MIDDLE_NAME))
					.with(FormField.GENDER, FormFieldRepository.getFieldId(realm, FormField.GENDER))
					.with(FormField.DATE_OF_BIRTH, FormFieldRepository.getFieldId(realm, FormField.DATE_OF_BIRTH))

					.with(FormField.ADDRESS, FormFieldRepository.getFieldId(realm, FormField.ADDRESS))
					.with(FormField.CITY, FormFieldRepository.getFieldId(realm, FormField.CITY))
					.with(FormField.STATE, FormFieldRepository.getFieldId(realm, FormField.STATE))
					.with(FormField.COUNTRY, FormFieldRepository.getFieldId(realm, FormField.COUNTRY))

					.with(FormField.FACEBOOK_PROFILE, FormFieldRepository.getFieldId(realm, FormField.FACEBOOK_PROFILE))
					.with(FormField.TWITTER_PROFILE, FormFieldRepository.getFieldId(realm, FormField.TWITTER_PROFILE))
					.with(FormField.LINKEDIN_PROFILE, FormFieldRepository.getFieldId(realm, FormField.LINKEDIN_PROFILE))
					.with(FormField.SKYPE_PROFILE, FormFieldRepository.getFieldId(realm, FormField.SKYPE_PROFILE));

		}

		if (realm instanceof AdminRealm) {
			result.with(FormField.ORGANIZATION_ID, FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_ID))
					.with(FormField.ORGANIZATION_NAME,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_NAME))
					.with(FormField.ORGANIZATION_EMAIL,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_EMAIL))
					.with(FormField.ORGANIZATION_PHONE,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_PHONE))
					.with(FormField.ORGANIZATION_LOGO,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_LOGO))
					.with(FormField.ORGANIZATION_ADDRESS,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_ADDRESS))
					.with(FormField.ORGANIZATION_POSTAL_CODE,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_POSTAL_CODE))
					.with(FormField.ORGANIZATION_CITY,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_CITY))
					.with(FormField.ORGANIZATION_STATE,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_STATE))
					.with(FormField.ORGANIZATION_COUNTRY,
							FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_COUNTRY));
		}

		if (realm instanceof AgentRealm) {
			result.with(FormField.ORGANIZATION_ID, FormFieldRepository.getFieldId(realm, FormField.ORGANIZATION_ID))
					.with(FormField.YEARS_OF_EXPERIENCE,
							FormFieldRepository.getFieldId(realm, FormField.YEARS_OF_EXPERIENCE));

		}

		return result;
	}

	private static void saveFieldId(Realm realm, FormField fieldType, String Id) {
		String key = FORM_FIELD_MAPPING_KEY_PREFIX + realm.name() + "_" + fieldType;
		try {
			ConfigModel.putIfNotExists(key, Id);
		} catch (ResourceException e) {
			Exceptions
			.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FUNCTIONALITY, fString, c.getName()));
		}
	}

	public static String getFieldId(Realm realm, FormField fieldType) {
		String key = FORM_FIELD_MAPPING_KEY_PREFIX + realm.name() + "_" + fieldType;
		Object value = ConfigModel.get(key);
		return value != null ? value.toString() : null;
	}

	public enum FormField {
		PREFERRED_LOCALE, IMAGE, FIRST_NAME, MIDDLE_NAME, LAST_NAME, DATE_OF_BIRTH, GENDER, EMAIL, PASSWORD,
		PHONE_NUMBER, ADDRESS, CITY, STATE, COUNTRY, POSTAL_CODE, ORGANIZATION_ID, ORGANIZATION_NAME,
		ORGANIZATION_EMAIL, ORGANIZATION_PHONE, ORGANIZATION_LOGO, ORGANIZATION_ADDRESS, ORGANIZATION_POSTAL_CODE,
		ORGANIZATION_CITY, ORGANIZATION_STATE, ORGANIZATION_COUNTRY, YEARS_OF_EXPERIENCE, FACEBOOK_PROFILE,
		TWITTER_PROFILE, LINKEDIN_PROFILE, SKYPE_PROFILE
	}

}
