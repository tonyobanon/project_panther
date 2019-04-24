package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.RBEntry;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.FormSectionType;
import com.re.paas.internal.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.helpers.FormFieldRepository;
import com.re.paas.internal.models.tables.forms.FormCompositeFieldTable;
import com.re.paas.internal.models.tables.forms.FormSectionTable;
import com.re.paas.internal.models.tables.forms.FormSimpleFieldTable;

public class FormModel extends BaseModel {

	@Override
	public String path() {
		return "core/form";
	}

	@Override
	public void preInstall() {
		FormFieldRepository.createDefaultFields();
	}

	@PlatformInternal
	public static Map<Realm, String> newSection(Object name, FormSectionType type) {

		AbstractRealmDelegate delegate = Realm.getDelegate();
		Map<Realm, String> result = new FluentHashMap<>();
		
		for (String realmName : delegate.getRealmNames()) {
			Realm realm = delegate.getRealm(realmName);
			result.put(realm, newSection(name, null, type, realm));
		}
		return result;
	}

	/**
	 * This creates a new section, for the given realm
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS,
			PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM })
	public static String newSection(Object title, Object description, FormSectionType type, Realm realm) {

		FormSectionTable e = new FormSectionTable().setId(Utils.newShortRandom()).setType(type.getValue())
				.setRealm(realm != null ? realm.name() : null);

		if (title instanceof ClientRBRef) {
			e.setTitle((ClientRBRef) title);
		}

		if (description != null & description instanceof ClientRBRef) {
			e.setDescription((ClientRBRef) description);
		}

		ofy().save().entity(e).now();

		boolean b = false;
		String locale = LocaleModel.getUserLocale();
		
		if (e.getTitle() == null) {

			String titleKey = "form_section_" + e.getId() + "_title";
			RBModel.newEntry(new RBEntry(titleKey, locale, title.toString()));

			e.setTitle(ClientRBRef.get(titleKey));
			b = true;
		}

		if (description != null && e.getDescription() == null) {

			String descriptionKey = "form_section_" + e.getId() + "_description";
			RBModel.newEntry(new RBEntry(descriptionKey, locale, description.toString()));

			e.setDescription(ClientRBRef.get(descriptionKey));
			b = true;
		}

		if (b) {
			ofy().save().entity(e);
		}

		return e.getId();
	}

	private static Integer getSectionType(String sectionId) {
		FormSectionTable e = ofy().load().type(FormSectionTable.class).id(sectionId).safe();
		return e.getType();
	}

	/**
	 * This lists all sections for the given realm
	 */
	@ModelMethod(functionality = { 
			UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM,
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static List<Section> listSections(FormSectionType type, Realm realm) {

		List<QueryFilter> filters = new FluentArrayList<>();
		filters.add(QueryFilter.get("type = ", type.getValue()));

		if (realm != null) {
			filters.add(QueryFilter.get("realm = ", realm.name()));
		}

		QueryFilter[] filtersArray = filters.toArray(new QueryFilter[filters.size()]);

		List<Section> result = new FluentArrayList<>();

		EntityUtils.query(FormSectionTable.class, filtersArray).forEach(e -> {
			result.add(new Section().setId(e.getId().toString()).setTitle(e.getTitle()).setSummary(e.getDescription()));
		});

		return result;
	}

	@ModelMethod(functionality = 
			UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void deleteSection(String sectionId, FormSectionType type) {

		FormSectionTable e = ofy().load().type(FormSectionTable.class).id(sectionId).safe();

		if (!e.getType().equals(type.getValue())) {
			throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
		}

		deleteSection(sectionId);
	}

	/**
	 * This deletes a section
	 */
	protected static void deleteSection(String sectionId) {

		// Delete fields

		listFieldKeys(sectionId).forEach(k -> {

			ofy().delete().key(k);
		});

		// Delete entity

		ofy().delete().key(Key.create(FormSectionTable.class, sectionId)).now();
	}

	/**
	 * This creates a new simple field
	 */
	protected static String newSimpleField(FormSectionType type, String sectionId, SimpleField spec,
			Boolean isDefault) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		// Create field

		FormSimpleFieldTable e = EntityHelper.fromObjectModel(sectionId, isDefault, spec);

		ofy().save().entity(e).now();

		// Ensure key uniqueness in FormCompositeFieldEntity

		if (ofy().load().type(FormCompositeFieldTable.class).id(e.getId()).now() != null) {

			Logger.get().warn("Duplicate key was created while creating simple form field. Recreating ..");

			ofy().delete().key(Key.create(FormSimpleFieldTable.class, e.getId())).now();
			return newSimpleField(type, sectionId, spec, isDefault);
		}

		if (e.getTitle() == null) {

			String titleKey = "form_simple_field_" + e.getId() + "_title";
			RBModel.newEntry(new RBEntry(titleKey, LocaleModel.getUserLocale(), spec.getTitle().toString()));

			e.setTitle(ClientRBRef.get(titleKey));

			ofy().save().entity(e);
		}

		return e.getId();
	}

	/**
	 * This creates a new simple field
	 */
	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static String newSimpleField(FormSectionType type, String sectionId, SimpleField spec) {
		return newSimpleField(type, sectionId, spec, false);
	}

	// Created for FormFieldRepository
	public static String newSimpleField(String sectionId, SimpleField spec) {
		return newSimpleField(FormSectionType.APPLICATION_FORM, sectionId, spec);
	}

	private static Map<String, ClientRBRef> listSimpleFieldNames(QueryFilter... filters) {

		Map<String, ClientRBRef> entries = new HashMap<>();

		EntityUtils.lazyQuery(FormSimpleFieldTable.class, filters).forEach(e -> {
			entries.put(e.getId(), e.getTitle());
		});

		return entries;
	}

	private static Map<String, ClientRBRef> listCompositeFieldNames(QueryFilter... filters) {

		Map<String, ClientRBRef> entries = new HashMap<>();

		EntityUtils.lazyQuery(FormCompositeFieldTable.class, filters).forEach(e -> {
			entries.put(e.getId(), e.getTitle());
		});

		return entries;
	}

	/**
	 * This lists all simple simple fields that matches the specified query filter
	 */
	private static List<SimpleField> listSimpleFields(QueryFilter... filters) {

		List<SimpleField> entries = new FluentArrayList<>();

		EntityUtils.lazyQuery(FormSimpleFieldTable.class, filters).forEach(e -> {
			entries.add(EntityHelper.toObjectModel(e));
		});

		return entries;
	}

	/**
	 * This creates a new composite field
	 */
	protected static String newCompositeField(FormSectionType type, String sectionId, CompositeField spec,
			Boolean isDefault) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		// Create field

		FormCompositeFieldTable e = EntityHelper.fromObjectModel(sectionId, isDefault, spec);

		ofy().save().entity(e).now();

		// Ensure key uniqueness in FormSimpleFieldEntity

		if (ofy().load().type(FormSimpleFieldTable.class).id(e.getId()).now() != null) {

			Logger.get().warn("Duplicate key was created while creating composite form field. Recreating ..");

			ofy().delete().type(FormCompositeFieldTable.class).id(e.getId()).now();

			return newCompositeField(type, sectionId, spec, isDefault);
		}

		boolean b = false;

		List<RBEntry> rbEntries = new ArrayList<RBEntry>();

		if (e.getTitle() == null) {

			String titleKey = "form_composite_field_" + e.getId() + "_title";
			rbEntries.add(new RBEntry(titleKey, LocaleModel.getUserLocale(), spec.getTitle().toString()));

			e.setTitle(ClientRBRef.get(titleKey));
			b = true;
		}

		if (e.getOptions().isEmpty()) {

			Map<ClientRBRef, Object> nOptions = new HashMap<>();

			int i = 1;
			for (Map.Entry<Object, Object> entry : spec.getItems().entrySet()) {
				String titleKey = "form_composite_field_" + e.getId() + "_opt_" + i;
				rbEntries.add(new RBEntry(titleKey, LocaleModel.getUserLocale(), entry.getKey().toString()));
				nOptions.put(ClientRBRef.get(titleKey), entry.getValue());
				i++;
			}

			e.setOptions(nOptions);
			b = true;
		}

		if (b) {
			RBModel.newEntry(rbEntries.toArray(new RBEntry[rbEntries.size()]));
			ofy().save().entity(e);
		}

		return e.getId();
	}

	/**
	 * This creates a new composite field
	 */
	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static String newCompositeField(FormSectionType type, String sectionId, CompositeField spec) {
		return newCompositeField(type, sectionId, spec, false);
	}

	// Created for FormFieldRepository
	public static String newCompositeField(String sectionId, CompositeField spec) {
		return newCompositeField(FormSectionType.APPLICATION_FORM, sectionId, spec);
	}

	/**
	 * This lists all composite fields that matches the specified query filter
	 */
	private static List<CompositeField> listCompositeFields(QueryFilter... filters) {

		List<CompositeField> entries = new FluentArrayList<>();

		EntityUtils.lazyQuery(FormCompositeFieldTable.class, filters).forEach(e -> {
			entries.add(EntityHelper.toObjectModel(e));
		});

		return entries;
	}

	protected static Map<String, Boolean> listAllFieldKeys(FormSectionType type, Realm realm) {

		Map<String, Boolean> keys = new FluentHashMap<>();

		listSections(type, realm).forEach(section -> {

			Map<String, Boolean> o = new FluentHashMap<>();

			EntityUtils.lazyQuery(FormSimpleFieldTable.class, QueryFilter.get("section =", section.getId()))
					.forEach(e -> {
						o.put(e.getId(), e.getIsRequired());
					});

			EntityUtils.lazyQuery(FormCompositeFieldTable.class, QueryFilter.get("section =", section.getId()))
					.forEach(e -> {
						o.put(e.getId(), e.getIsRequired());
					});

			keys.putAll(o);
		});

		return keys;
	}

	/**
	 * This lists the keys for all simple and composite fields that exists in a
	 * section
	 */
	private static List<Key<?>> listFieldKeys(String sectionId) {

		List<Key<?>> keys = new FluentArrayList<>();

		ofy().load().type(FormSimpleFieldTable.class).filter("section = ", sectionId).forEach(e -> {
			keys.add(Key.create(FormSimpleFieldTable.class, e.getId()));
		});

		ofy().load().type(FormCompositeFieldTable.class).filter("section = ", sectionId).forEach(e -> {
			keys.add(Key.create(FormCompositeFieldTable.class, e.getId()));
		});

		return keys;
	}

	/**
	 * This gets fields names available in the given section
	 */
	@ModelMethod(functionality = { 
			UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM, 
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static Map<String, ClientRBRef> getFieldNames(FormSectionType type, String sectionId) {

		FormSectionTable e = ofy().load().type(FormSectionTable.class).id(sectionId).safe();

		if (!e.getType().equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		Map<String, ClientRBRef> fields = new HashMap<String, ClientRBRef>();

		QueryFilter filter = QueryFilter.get("section =", sectionId);

		// Add simple fields
		fields.putAll(listSimpleFieldNames(filter));

		// Add composite fields
		fields.putAll(listCompositeFieldNames(filter));

		return fields;
	}

	/**
	 * This gets fields available in the given section
	 */
	@ModelMethod(functionality = { 
			UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM, 
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static List<AbstractField> getFields(FormSectionType type, String sectionId) {

		FormSectionTable e = ofy().load().type(FormSectionTable.class).id(sectionId).safe();

		if (!e.getType().equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		List<AbstractField> fields = new FluentArrayList<>();

		QueryFilter filter = QueryFilter.get("section =", sectionId);

		// Add simple fields
		fields.addAll(listSimpleFields(filter));

		// Add composite fields
		fields.addAll(listCompositeFields(filter));

		return fields;
	}

	/**
	 * This gets all fields available in the given section(s)
	 */
	@ModelMethod(functionality = { 
			UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM, 
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static Map<String, List<AbstractField>> getAllFields(FormSectionType type, List<String> sectionIds) {

		Map<String, List<AbstractField>> result = new FluentHashMap<>();

		sectionIds.forEach(sectionId -> {

			result.put(sectionId, getFields(type, sectionId));
		});

		return result;
	}

	/**
	 * This deletes a given field
	 */
	@ModelMethod(functionality = 
			UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS
			)
	public static void deleteField(FormSectionType type, String id) {

		boolean isDeleted = false;

		// Delete field entity

		FormSimpleFieldTable se = ofy().load().type(FormSimpleFieldTable.class).id(id).now();
		if (se != null && !se.getIsDefault()) {

			if (!getSectionType(se.getSection()).equals(type.getValue())) {
				throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
			}

			ofy().delete().key(Key.create(FormSimpleFieldTable.class, id)).now();
			isDeleted = true;
		}

		if (se == null) {
			FormCompositeFieldTable ce = ofy().load().type(FormCompositeFieldTable.class).id(id).now();
			if (ce != null && !ce.getIsDefault()) {

				if (!getSectionType(ce.getSection()).equals(type.getValue())) {
					throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
				}

				ofy().delete().key(Key.create(FormCompositeFieldTable.class, id)).now();
				isDeleted = true;
			}
		}

		// Then, delete delete saved values
		if (isDeleted) {
			if (type.equals(FormSectionType.APPLICATION_FORM)) {
				BaseUserModel.deleteFieldValues(id);
				ApplicationModel.deleteFieldValues(id);
			}
		} else {
			// <id> may be a default field
			throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
		}
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
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
