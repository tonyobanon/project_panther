package com.re.paas.integrated.models;

import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.M;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.N;
import static com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder.S;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.re.paas.api.annotations.develop.PlatformInternal;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.FieldType;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.forms.input.InputType;
import com.re.paas.api.infra.database.document.Database;
import com.re.paas.api.infra.database.document.Item;
import com.re.paas.api.infra.database.document.Table;
import com.re.paas.api.infra.database.document.xspec.DeleteItemSpec;
import com.re.paas.api.infra.database.document.xspec.ExpressionSpecBuilder;
import com.re.paas.api.infra.database.document.xspec.GetItemSpec;
import com.re.paas.api.infra.database.document.xspec.PutItemSpec;
import com.re.paas.api.infra.database.document.xspec.QuerySpec;
import com.re.paas.api.infra.database.model.BatchWriteItemRequest;
import com.re.paas.api.infra.database.model.ReturnValue;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.RBEntry;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Collections;
import com.re.paas.integrated.fusion.functionalities.PlatformFunctionalities;
import com.re.paas.integrated.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.integrated.models.helpers.FormFieldRepository;
import com.re.paas.integrated.tables.defs.base.RBEntryTable;
import com.re.paas.integrated.tables.defs.forms.FormCompositeFieldTable;
import com.re.paas.integrated.tables.defs.forms.FormSectionTable;
import com.re.paas.integrated.tables.defs.forms.FormSimpleFieldTable;
import com.re.paas.integrated.tables.spec.base.RBEntryTableSpec;
import com.re.paas.integrated.tables.spec.forms.FormCompositeFieldTableSpec;
import com.re.paas.integrated.tables.spec.forms.FormSectionTableSpec;
import com.re.paas.integrated.tables.spec.forms.FormSimpleFieldTableSpec;
import com.re.paas.internal.classes.FormSectionType;

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
		return newSection(name, null, type);
	}

	@PlatformInternal
	public static Map<Realm, String> newSection(Object name, Object description, FormSectionType type) {

		AbstractRealmDelegate delegate = Realm.getDelegate();
		Map<Realm, String> result = new FluentHashMap<>();

		for (String realmName : delegate.getRealmNames()) {
			Realm realm = delegate.getRealm(realmName);
			result.put(realm, newSection(name, description, type, realm));
		}

		return result;
	}

	/**
	 * This creates a new section, for the given realm
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS,
			PlatformFunctionalities.Constants.MANAGE_SYSTEM_CONFIGURATION_FORM })
	public static String newSection(Object title, Object description, FormSectionType type, Realm realm) {

		Date now = Dates.now();

		Table table = Database.get().getTable(FormSectionTable.class);
		Item i = new Item().withInt(FormSectionTableSpec.TYPE, type.getValue())
				.withDate(FormSectionTableSpec.DATE_CREATED, now);

		if (realm != null) {
			i.withString(FormSectionTableSpec.REALM, realm.name());
		}

		if (title instanceof ClientRBRef) {
			i.withString(FormSectionTableSpec.TITLE, title.toString());
		}

		if (description != null & description instanceof ClientRBRef) {
			i.withString(FormSectionTableSpec.DESCRIPTION, description.toString());
		}

		i = table.putItem(PutItemSpec.forItem(i).withReturnValues(ReturnValue.ALL_NEW)).getItem();

		String sectionId = i.getString(FormSectionTableSpec.ID);

		String locale = LocaleModel.getUserLocale();

		List<RBEntry> rbEntries = new ArrayList<>();

		if (title instanceof String) {

			String titleKey = "form_section_" + sectionId + "_title";
			rbEntries.add(new RBEntry(titleKey, locale, title.toString()));

			table.updateItem(new ExpressionSpecBuilder().withCondition(S(FormSectionTableSpec.ID).eq(sectionId))
					.addUpdate(S(FormSectionTableSpec.TITLE).set(titleKey)).buildForUpdate());
		}

		if (description != null && description instanceof String) {

			String descriptionKey = "form_section_" + sectionId + "_description";
			rbEntries.add(new RBEntry(descriptionKey, locale, description.toString()));

			table.updateItem(new ExpressionSpecBuilder().withCondition(S(FormSectionTableSpec.ID).eq(sectionId))
					.addUpdate(S(FormSectionTableSpec.DESCRIPTION).set(descriptionKey)).buildForUpdate());
		}

		Database.get().batchWriteItem(new BatchWriteItemRequest().putAll(RBEntryTable.class, rbEntries.stream()
				.map(e -> new Item().with(RBEntryTableSpec.KEY, e.getKey()).with(RBEntryTableSpec.LOCALE, e.getLocale())
						.with(RBEntryTableSpec.VALUE, e.getValue()).with(RBEntryTableSpec.DATE_CREATED, now))
				.collect(Collectors.toList())));

		return sectionId;
	}

	private static Integer getSectionType(String sectionId) {
		return Database.get().getTable(FormSectionTable.class)
				.getItem(GetItemSpec.forKey(FormSectionTableSpec.ID, sectionId, FormSectionTableSpec.TYPE))
				.getInt(FormSectionTableSpec.TYPE);
	}

	/**
	 * This lists all sections for the given realm
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM,
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static List<Section> listSections(FormSectionType type, Realm realm) {

		ExpressionSpecBuilder expr = new ExpressionSpecBuilder()
				.withKeyCondition(N(FormSectionTableSpec.TYPE).eq(type.getValue()));

		if (realm != null) {
			expr.withCondition(S(FormSectionTableSpec.REALM).eq(realm.name()));
		}

		List<Section> result = new FluentArrayList<>();

		Database.get().getTable(FormSectionTable.class).getIndex(FormSectionTableSpec.TYPE_INDEX)
				.all(expr.buildForQuery()).forEach(i -> {

					result.add(new Section().setId(i.getString(FormSectionTableSpec.ID))
							.setTitle(i.getString(FormSectionTableSpec.TITLE))
							.setSummary(i.getString(FormSectionTableSpec.DESCRIPTION)));
				});

		return result;
	}

	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void deleteSection(String sectionId, FormSectionType type) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
		}

		deleteSection(sectionId);
	}

	/**
	 * This deletes a section
	 */
	protected static void deleteSection(String sectionId) {

		// Delete fields

		deleteFields(sectionId);

		// Delete entity
		Database.get().getTable(FormSectionTable.class)
				.deleteItem(DeleteItemSpec.forKey(FormSectionTableSpec.ID, sectionId));
	}

	/**
	 * This creates a new simple field
	 */
	protected static String newSimpleField(FormSectionType type, String sectionId, SimpleField spec,
			Boolean isDefault) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		Table table = Database.get().getTable(FormSimpleFieldTable.class);

		// Create field

		Item i = new Item()

				.with(FormSimpleFieldTableSpec.ID, spec.getId()).with(FormSimpleFieldTableSpec.SECTION, sectionId)
				.with(FormSimpleFieldTableSpec.INPUT_TYPE, spec.getInputType().getValue())
				.with(FormSimpleFieldTableSpec.SORT_ORDER, spec.getSortOrder())
				.with(FormSimpleFieldTableSpec.DEFAULT_VALUE, spec.getDefaultValue())
				.with(FormSimpleFieldTableSpec.IS_REQUIRED, spec.getIsRequired())
				.with(FormSimpleFieldTableSpec.IS_VISIBLE, spec.getIsVisible());

		if (isDefault) {
			i.with(FormSimpleFieldTableSpec.IS_DEFAULT, true);
		}

		i.withDate(FormSimpleFieldTableSpec.DATE_CREATED, Dates.now());

		if (spec.getTitle() instanceof ClientRBRef) {
			i.with(FormSimpleFieldTableSpec.TITLE, spec.getTitle());
		}

		String id = table.putItem(PutItemSpec.forItem(i)).getItem().getString(FormSimpleFieldTableSpec.ID);

		// Ensure key uniqueness in FormCompositeFieldTable

		if (Database.get().getTable(FormCompositeFieldTable.class)
				.getItem(GetItemSpec.forKey(FormCompositeFieldTableSpec.ID, id)) != null) {

			Logger.get().warn("Duplicate field key was created while creating simple form field. Recreating ..");

			Database.get().getTable(FormSimpleFieldTable.class)
					.deleteItem(DeleteItemSpec.forKey(FormSimpleFieldTableSpec.ID, id));

			return newSimpleField(type, sectionId, spec, isDefault);
		}

		if (spec.getTitle() instanceof String) {

			String titleKey = "form_simple_field_" + id + "_title";
			RBModel.newEntry(new RBEntry(titleKey, LocaleModel.getUserLocale(), (String) spec.getTitle()));

			table.updateItem(new ExpressionSpecBuilder().withCondition(S(FormSimpleFieldTableSpec.ID).eq(id))
					.addUpdate(S(FormSimpleFieldTableSpec.TITLE).set(titleKey)).buildForUpdate());
		}

		return id;
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

	/**
	 * This creates a new composite field
	 */
	protected static String newCompositeField(FormSectionType type, String sectionId, CompositeField spec,
			Boolean isDefault) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		// Ensure that all options values have the same type: ClientRBRef | String
		spec.getItems().values().stream().reduce((acc, v) -> {
			if (!v.getClass().isAssignableFrom(acc.getClass())) {
				throw new IllegalArgumentException("All option values must have the same type");
			}
			return v;
		});

		Table table = Database.get().getTable(FormCompositeFieldTable.class);

		// Create field

		Item item = new Item()

				.with(FormCompositeFieldTableSpec.ID, spec.getId()).with(FormCompositeFieldTableSpec.SECTION, sectionId)
				.with(FormCompositeFieldTableSpec.SORT_ORDER, spec.getSortOrder())
				.with(FormCompositeFieldTableSpec.IS_REQUIRED, spec.getIsRequired())
				.with(FormCompositeFieldTableSpec.IS_VISIBLE, spec.getIsVisible());

		if (isDefault) {
			item.with(FormCompositeFieldTableSpec.IS_DEFAULT, true);
		}

		item.with(FormCompositeFieldTableSpec.ITEM_SOURCE, spec.getItemsSource())
				.with(FormCompositeFieldTableSpec.DEFAULT_SELECTIONS, spec.getDefaultSelections())
				.with(FormCompositeFieldTableSpec.ALLOW_MULTIPLE_CHOICE, spec.isAllowMultipleChoice())

				.withDate(FormCompositeFieldTableSpec.DATE_CREATED, Dates.now());

		if (spec.getTitle() instanceof ClientRBRef) {
			item.with(FormCompositeFieldTableSpec.TITLE, spec.getTitle());
		}

		if (Collections.firstValue(spec.getItems()) instanceof ClientRBRef) {
			Map<String, String> options = new HashMap<String, String>();
			spec.getItems().forEach((k, v) -> {
				options.put(k, ((ClientRBRef) v).toString());
			});
			item.withMap(FormCompositeFieldTableSpec.OPTIONS, options);
		}

		String id = table.putItem(PutItemSpec.forItem(item)).getItem().getString(FormCompositeFieldTableSpec.ID);

		// Ensure key uniqueness in FormSimpleFieldTable

		if (Database.get().getTable(FormSimpleFieldTable.class)
				.getItem(GetItemSpec.forKey(FormSimpleFieldTableSpec.ID, id)) != null) {

			Logger.get().warn("Duplicate field key was created while creating simple form field. Recreating ..");

			Database.get().getTable(FormCompositeFieldTable.class)
					.deleteItem(DeleteItemSpec.forKey(FormCompositeFieldTableSpec.ID, id));

			return newCompositeField(type, sectionId, spec, isDefault);
		}

		List<RBEntry> rbEntries = new ArrayList<RBEntry>();

		if (spec.getTitle() instanceof String) {

			String titleKey = "form_composite_field_" + id + "_title";
			rbEntries.add(new RBEntry(titleKey, LocaleModel.getUserLocale(), (String) spec.getTitle()));

			table.updateItem(new ExpressionSpecBuilder().withCondition(S(FormCompositeFieldTableSpec.ID).eq(id))
					.addUpdate(S(FormCompositeFieldTableSpec.TITLE).set(titleKey)).buildForUpdate());
		}

		if (Collections.firstValue(spec.getItems()) instanceof String) {

			Map<String, String> options = new HashMap<>();

			int i = 1;
			for (Map.Entry<String, Object> entry : spec.getItems().entrySet()) {
				String titleKey = "form_composite_field_" + id + "_opt_" + i;
				rbEntries.add(new RBEntry(titleKey, LocaleModel.getUserLocale(), (String) entry.getValue()));
				options.put(entry.getKey(), titleKey);
				i++;
			}

			table.updateItem(new ExpressionSpecBuilder().withCondition(S(FormCompositeFieldTableSpec.ID).eq(id))
					.addUpdate(M(FormCompositeFieldTableSpec.OPTIONS).set(options)).buildForUpdate());
		}

		if (!rbEntries.isEmpty()) {
			RBModel.newEntry(rbEntries.toArray(new RBEntry[rbEntries.size()]));
		}

		return id;
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

	protected static Map<String, Boolean> listAllFieldKeys(FormSectionType type, Realm realm) {

		Map<String, Boolean> keys = new FluentHashMap<>();

		listSections(type, realm).forEach(section -> {

			Map<String, Boolean> o = getFieldsAttribute(section.getId(), null, (fType, id) -> {
				return ((Boolean) getFieldAttribute(fType, id,
						fType == FieldType.SIMPLE ? FormSimpleFieldTableSpec.IS_REQUIRED
								: FormCompositeFieldTableSpec.IS_REQUIRED));
			});

			keys.putAll(o);
		});

		return keys;
	}

	private static <T> Map<String, T> getFieldsAttribute(String sectionId, FieldType type,
			BiFunction<FieldType, String, T> mapper) {

		FieldType[] fieldTypes = new FieldType[] { type != null ? type : FieldType.SIMPLE, FieldType.COMPOSITE };
		Map<String, T> result = new HashMap<>();

		if (Arrays.binarySearch(fieldTypes, FieldType.SIMPLE) >= 0) {

			Database.get().getTable(FormSimpleFieldTable.class).getIndex(FormSimpleFieldTableSpec.SECTION_INDEX)
					.all(QuerySpec.get(FormSimpleFieldTableSpec.SECTION, sectionId, FormSimpleFieldTableSpec.ID))
					.forEach(i -> {
						String id = i.getString(FormSimpleFieldTableSpec.ID);
						result.put(id, mapper != null ? mapper.apply(FieldType.SIMPLE, id) : null);
					});
		}

		if (Arrays.binarySearch(fieldTypes, FieldType.COMPOSITE) >= 0) {
			Database.get().getTable(FormCompositeFieldTable.class).getIndex(FormCompositeFieldTableSpec.SECTION_INDEX)
					.all(QuerySpec.get(FormCompositeFieldTableSpec.SECTION, sectionId, FormCompositeFieldTableSpec.ID))
					.forEach(i -> {
						String id = i.getString(FormCompositeFieldTableSpec.ID);
						result.put(id, mapper != null ? mapper.apply(FieldType.COMPOSITE, id) : null);
					});
		}

		return result;
	}

	/**
	 * This lists the keys for all simple and composite fields that exists in a
	 * section
	 */
	private static void deleteFields(String sectionId) {

		BatchWriteItemRequest batchFieldsDelete = new BatchWriteItemRequest();

		Set<String> simpleFieldIds = getFieldsAttribute(sectionId, FieldType.SIMPLE, null).keySet();
		batchFieldsDelete.deleteAll(FormSimpleFieldTable.class, FormSimpleFieldTableSpec.ID,
				(Object[]) simpleFieldIds.toArray(new String[simpleFieldIds.size()]));

		Set<String> compositeFieldIds = getFieldsAttribute(sectionId, FieldType.COMPOSITE, null).keySet();
		batchFieldsDelete.deleteAll(FormCompositeFieldTable.class, FormCompositeFieldTableSpec.ID,
				(Object[]) compositeFieldIds.toArray(new String[compositeFieldIds.size()]));

		Database.get().batchWriteItem(batchFieldsDelete);
	}

	private static Object getFieldAttribute(FieldType type, String id, String attrName) {
		switch (type) {
		case COMPOSITE:
			return Database.get().getTable(FormCompositeFieldTable.class)
					.getItem(GetItemSpec.forKey(FormCompositeFieldTableSpec.ID, id, attrName)).get(attrName);
		case SIMPLE:
		default:
			return Database.get().getTable(FormSimpleFieldTable.class)
					.getItem(GetItemSpec.forKey(FormSimpleFieldTableSpec.ID, id, attrName)).get(attrName);
		}
	}

	private static FieldType getFieldType(String id) {

		if (Database.get().getTable(FormCompositeFieldTable.class).getItem(
				GetItemSpec.forKey(FormCompositeFieldTableSpec.ID, id, FormCompositeFieldTableSpec.ID)) != null) {
			return FieldType.COMPOSITE;
		}

		if (Database.get().getTable(FormSimpleFieldTable.class)
				.getItem(GetItemSpec.forKey(FormSimpleFieldTableSpec.ID, id, FormSimpleFieldTableSpec.ID)) != null) {
			return FieldType.SIMPLE;
		}

		return null;
	}

	/**
	 * This gets fields names available in the given section
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM,
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static Map<String, ClientRBRef> getFieldTitle(FormSectionType type, String sectionId) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		Map<String, ClientRBRef> fields = new HashMap<String, ClientRBRef>();

		getFieldsAttribute(sectionId, null, (fType, id) -> {
			return ClientRBRef.get(getFieldAttribute(fType, id,
					fType == FieldType.SIMPLE ? FormSimpleFieldTableSpec.TITLE : FormCompositeFieldTableSpec.TITLE));
		}).forEach((k, v) -> {
			fields.put(k, v);
		});

		return fields;
	}

	public static CompositeField getCompositeField(String id) {

		Item item = Database.get().getTable(FormCompositeFieldTable.class)
				.getItem(GetItemSpec.forKey(FormCompositeFieldTableSpec.ID, id));

		CompositeField o = new CompositeField(item.getString(FormCompositeFieldTableSpec.ID),
				ClientRBRef.get(item.getString(FormCompositeFieldTableSpec.TITLE)))

						.setItemsSource(item.getString(FormCompositeFieldTableSpec.ITEM_SOURCE))
						.setDefaultSelections(item.getList(FormCompositeFieldTableSpec.DEFAULT_SELECTIONS))
						.setAllowMultipleChoice(item.getBoolean(FormCompositeFieldTableSpec.ALLOW_MULTIPLE_CHOICE));

		for (Map.Entry<String, Object> e : item.getMap(FormCompositeFieldTableSpec.OPTIONS).entrySet()) {
			o.withItem(e.getKey(), ClientRBRef.get(e.getValue()));
		}

		o.setSortOrder(item.getInt(FormCompositeFieldTableSpec.SORT_ORDER));
		o.setIsRequired(item.getBoolean(FormCompositeFieldTableSpec.IS_REQUIRED));
		o.setIsVisible(item.getBoolean(FormCompositeFieldTableSpec.IS_VISIBLE));
		o.setIsDefault(item.getBoolean(FormCompositeFieldTableSpec.IS_DEFAULT));

		return o;
	}

	public static SimpleField getSimpleField(String id) {

		Item item = Database.get().getTable(FormSimpleFieldTable.class)
				.getItem(GetItemSpec.forKey(FormSimpleFieldTableSpec.ID, id));

		SimpleField o = new SimpleField(item.getString(FormSimpleFieldTableSpec.ID),
				InputType.from(item.getInt(FormSimpleFieldTableSpec.INPUT_TYPE)),
				ClientRBRef.get(item.getString(FormSimpleFieldTableSpec.TITLE)))

						.setDefaultValue(item.getString(FormSimpleFieldTableSpec.DEFAULT_VALUE));

		o.setSortOrder(item.getInt(FormSimpleFieldTableSpec.SORT_ORDER));
		o.setIsRequired(item.getBoolean(FormSimpleFieldTableSpec.IS_REQUIRED));
		o.setIsVisible(item.getBoolean(FormSimpleFieldTableSpec.IS_VISIBLE));
		o.setIsDefault(item.getBoolean(FormSimpleFieldTableSpec.IS_DEFAULT));

		return o;
	}

	/**
	 * This gets fields Ids available in the given section
	 */
	public static Collection<String> getFieldIds(FormSectionType type, String sectionId) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		return getFieldsAttribute(sectionId, null, (fType, id) -> null).keySet();
	}
	
	/**
	 * This gets fields available in the given section
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM,
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static Map<String, AbstractField> getFields(FormSectionType type, String sectionId) {

		if (!getSectionType(sectionId).equals(type.getValue())) {
			throw new ResourceException(ResourceException.ACCESS_NOT_ALLOWED);
		}

		return getFieldsAttribute(sectionId, null, (fType, id) -> {
			return fType == FieldType.SIMPLE ? getSimpleField(id) : getCompositeField(id);
		});
	}

	/**
	 * This gets all fields available in the given section(s)
	 */
	@ModelMethod(functionality = { UserApplicationFunctionalities.Constants.VIEW_APPLICATION_FORM,
			PlatformFunctionalities.Constants.VIEW_SYSTEM_CONFIGURATION })
	public static Map<String, Map<String, AbstractField>> getAllFields(FormSectionType type, List<String> sectionIds) {

		Map<String, Map<String, AbstractField>> result = new FluentHashMap<>();

		sectionIds.forEach(sectionId -> {
			result.put(sectionId, getFields(type, sectionId));
		});

		return result;
	}

	/**
	 * This deletes a given field
	 */
	@ModelMethod(functionality = UserApplicationFunctionalities.Constants.MANAGE_APPLICATION_FORMS)
	public static void deleteField(FormSectionType type, String id) {

		FieldType fType = getFieldType(id);

		if (fType == null) {
			throw new ResourceException(ResourceException.RESOURCE_NOT_FOUND);
		}

		if (((Boolean) getFieldAttribute(fType, id, fType == FieldType.SIMPLE ? FormSimpleFieldTableSpec.IS_DEFAULT
				: FormCompositeFieldTableSpec.IS_DEFAULT))) {
			throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
		}

		Integer sType = getSectionType((String) getFieldAttribute(fType, id,
				fType == FieldType.SIMPLE ? FormSimpleFieldTableSpec.SECTION : FormCompositeFieldTableSpec.SECTION));

		if (!sType.equals(type.getValue())) {
			throw new ResourceException(ResourceException.DELETE_NOT_ALLOWED);
		}

		switch (fType) {
		case COMPOSITE:
			Database.get().getTable(FormCompositeFieldTable.class)
					.deleteItem(DeleteItemSpec.forKey(FormCompositeFieldTableSpec.ID, id));
			break;
		case SIMPLE:
			Database.get().getTable(FormSimpleFieldTable.class)
					.deleteItem(DeleteItemSpec.forKey(FormSimpleFieldTableSpec.ID, id));
			break;
		}

		if (type.equals(FormSectionType.APPLICATION_FORM)) {
			BaseUserModel.deleteFieldValues(id);
			ApplicationModel.deleteFieldValues(id);
		}
	}

	@Override
	public void install(InstallOptions options) {

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
