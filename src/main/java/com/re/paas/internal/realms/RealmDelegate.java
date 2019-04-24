package com.re.paas.internal.realms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.BaseSimpleField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Reference;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.errors.RealmError;

@DelegateSpec(dependencies = { SpiType.FUNCTIONALITY })
public class RealmDelegate extends AbstractRealmDelegate {

	private static final String REFERENCE_DELIMETER = " -> ";

	private static final String FORM_FIELD_OBJECTS_NAMESPACE = "ffo";
	private static final String FORM_SECTION_OBJECTS_NAMESPACE = "fso";

	private static final String FORM_ELEMENT_MAPPING_NAMESPACE = "feMapping";

	private static final String FUNCTIONALITIES_NAMESPACE = "functionalities";
	private static final String FORM_SECTION_NAMESPACE = "form_section";

	private static final String ROLE_FUNCTIONALITIES_NAMESPACE = "roleFunctionalities";
	private static final String LOADED_REALM_CLASSES_NAMESPACE = "loadedClasses";
	private static final String REALM_OBJECT_NAMESPACE = "object";

	private static final Logger LOGGER = Logger.get(RealmDelegate.class);
	private static final boolean ALLOW_MULTI_REFERENCING = false;

	@Override
	public DelegateInitResult init() {
		createResourceMaps();
		add(getResourceClasses());
		return DelegateInitResult.SUCCESS;
	}

	@Override
	protected void add(List<Class<Realm>> classes) {
		
		List<Realm> realms = new ArrayList<>();

		classes.forEach(c -> {
			realms.add(ClassUtils.createInstance(c));
		});

		realms.forEach(this::validateForm);
		realms.forEach(this::addRealm);

		// We need to run serially, not parallel
		for (Realm realm : realms) {
			registerFormSections(realm);
		}
	}

	@Override
	protected List<Class<Realm>> remove(List<Class<Realm>> classes) {
		classes.forEach(c -> {
			removeRealm(ClassUtils.createInstance(c));
		});
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getRealmNames() {
		return getRealmObjectMap().keySet();
	}

	@Override
	public Realm getRealm(String name) {
		return getRealmObjectMap().get(name);
	}

	@Override
	public Collection<Section> getSections(Realm realm) {
		return getFormSectionMap().get(realm.name()).values();
	}

	private void addRealm(Realm realm) {

		if (!realm.enabled()) {
			return;
		}

		if (Realm.isBaseRealm(realm)) {

			Realm existing = getRealmObjectMap().get(realm.name());

			if (existing != null) {

				// We need to ensure that the existing realm is of the same class
				// with this

				if (!existing.getClass().getName().equals(realm.getClass().getName())) {

					Exceptions.throwRuntime(PlatformException.get(RealmError.REALM_NAME_ALREADY_EXISTS, realm.name(),
							realm.getClass().getName()));
				}

			} else {

				getFunctionalitiesMap().put(realm.name(), new ArrayList<>());
				getFormSectionMap().put(realm.name(), new LinkedHashMap<>());

				// Add to realm map
				getRealmObjectMap().put(realm.name(), realm);
			}

		} else {

			@SuppressWarnings("unchecked")
			Class<? extends Realm> parent = (Class<? extends Realm>) realm.getClass().getSuperclass();

			addRealm(ClassUtils.createInstance(parent));
		}

		if (getRealmObjectMap().get(realm.name()) == null) {

			// The subclass and its parent cannot have different realm names
			Exceptions.throwRuntime(
					PlatformException.get(RealmError.REALM_NAME_MISMATCH, realm.getClass().getName(), realm.name()));
		}
		
		
		// Check realm spec
		//ClassUtils.
		Class clazz;
		
		
		
		// Add functionalities

		List<String> functionalities = getFunctionalitiesMap().get(realm.name());

		for (Functionality f : realm.functionalities()) {

			String fString = f.asString();

			if (functionalities.contains(fString)) {
				
				// Fail-fast if f already exist, since this could potentially cause issues
				// when the app is uninstalled
				Exceptions.throwRuntime(
						PlatformException.get(RealmError.DUPLICATE_FUNCTIONALITY, fString, realm.getClass().getName()));
			}

			functionalities.add(fString);
		}
	}

	private void registerFormSections(Realm realm) {

		String realmClass = realm.getClass().getName();
		List<String> loadedClasses = getLoadedRealmClasses();

		for (Section s : realm.onboardingForm()) {

			String refClass = s.getReference().realm().getClass().getName();

			boolean isExtRealm = !refClass.equals(realmClass);
			boolean isLoaded = loadedClasses.contains(refClass);

			if (isExtRealm && !isLoaded) {
				registerFormSections(realm);
			}
		}

		loadedClasses.add(realmClass);

		for (Section s : realm.onboardingForm()) {
			registerFormSection(realm, s);
		}
	}

	private String getReferenceKey(Realm realm, SectionReference sReference) {
		return getReferenceKey(realm, sReference, null);
	}

	private String getReferenceKey(Realm realm, SectionReference sReference, Reference reference) {

		if (sReference.value().contains(REFERENCE_DELIMETER) || reference.value().contains(REFERENCE_DELIMETER)) {
			throw new IllegalArgumentException("Reference value(s) cannot contain: " + REFERENCE_DELIMETER);
		}

		StringBuilder key = new StringBuilder().append(realm.name()).append(REFERENCE_DELIMETER)
				.append(sReference.value());

		if (reference != null) {
			assert !(reference instanceof SectionReference);
			key.append(REFERENCE_DELIMETER).append(reference.value());
		}

		return key.toString();
	}

	private void saveReferenceMapping(Realm realm, SectionReference sReference, String Id) {
		saveReferenceMapping(realm, sReference, null, Id);
	}

	private void saveReferenceMapping(Realm realm, SectionReference sReference, Reference reference, String Id) {

		String key = getReferenceKey(realm, sReference, reference);

		Map<String, String> mappings = getFormElementMapping();

		if (mappings.get(key) != null) {
			Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_ELEMENT_IDENTIFIER,
					reference.value(), realm.getClass().getName()));
		}

		mappings.put(key, Id);
	}

	@Override
	public String getReferenceId(Realm realm, SectionReference sReference, Reference reference) {

		StringBuilder key = new StringBuilder().append(realm.name()).append(REFERENCE_DELIMETER)
				.append(sReference.value()).append(REFERENCE_DELIMETER).append(reference.value());

		return getFormElementMapping().get(key.toString());
	}

	private String getReferenceId(String referenceKey) {
		return getFormElementMapping().get(referenceKey);
	}

	private boolean validateField(BaseSimpleField field, boolean reference) {

		if (field.getReference() == null) {
			field.setReference(Reference.empty());
		}

		if (field.getReference().equals(Reference.empty())) {
			return false;
		}

		if (!reference) {

			if (field.getTitle() == null) {
				return false;
			}

			if (field instanceof SimpleField && ((SimpleField) field).getInputType() == null) {
				return false;
			}

			if (field instanceof CompositeField) {
				CompositeField composite = (CompositeField) field;
				if (composite.getItemsSource() == null && composite.getItems().isEmpty()) {
					return false;
				}
			}
		}

		return true;
	}

	private void newField(Realm realm, SectionReference sReference, BaseSimpleField field) {

		if (!validateField(field, false)) {
			Exceptions.throwRuntime(PlatformException.get(RealmError.INVALIDATE_FORM_FIELD,
					getReferenceKey(realm, sReference, field.getReference()), realm.getClass().getName()));
		}

		if (field.getId() != null) {
			LOGGER.warn("The field with reference: " + getReferenceKey(realm, sReference, field.getReference())
					+ " has an id: " + field.getId() + " which will be ignored");
		}

		// Generate field id
		field.setId(Utils.newShortRandom());

		// Save reference
		saveReferenceMapping(realm, sReference, field.getReference(), field.getId());

		// Save field object
		getFormFieldObjects().put(field.getId(), field);
	}

	private static boolean match(Realm first, Realm second) {
		// Due to the manner in which section referencing is made, the
		// name is always used for comparison
		return first.name().endsWith(second.name());
	}

	private void newSection(Realm realm, Section section) {

		SectionReference sReference = section.getReference();

		if (match(realm, sReference.realm())) {

			// Scenario *2
			// In this scenario, the section reference belong to the current realm

			// Generate section Id
			section.setId(Utils.newShortRandom());

			// Register fields

			section.getFields(BaseSimpleField.class).forEach(field -> {
				newField(realm, sReference, field);
			});

			// Save section object
			getFormSectionObjects().put(section.getId(), section);

		} else {

			// Scenario *3
			// In this scenario, the section reference belong to another realm

			// Use the id from the section reference
			String sectionId = getReferenceId(getReferenceKey(sReference.realm(), sReference));

			if (sectionId == null) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_SECTION_REFERENCE,
						getReferenceKey(realm, sReference), realm.getClass().getName()));
			}

			Section source = getSection(realm, sReference);

			// Analyze references
			analyzeReferences(source, realm, section);
		}

		// Save reference mapping
		saveReferenceMapping(realm, sReference, section.getId());

		// Save section object to realm

		Map<String, Section> formSectionMap = getFormSectionMap().get(realm.name());

		if (formSectionMap.containsKey(sReference.value())) {
			Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_SECTION,
					getReferenceKey(realm, sReference), realm.getClass().getName()));
		}

		formSectionMap.put(sReference.value(), section);
	}

	private void analyzeReferences(Section source, Realm realm, Section destination) {

		// Import section data
		destination.importData(source, destination.getReference().importFields());

		ListIterator<AbstractField> it = destination.getFields().listIterator();

		while (it.hasNext()) {

			BaseSimpleField field = (BaseSimpleField) it.next();

			// Attempt to get id, assuming the field is inherited from the source section
			String fieldId = getReferenceId(source.getReference().realm(), source.getReference(), field.getReference());

			if (fieldId == null) {

				// Then, this field does not contain a soft reference
				newField(realm, destination.getReference(), field);

			} else {

				if (!validateField(field, true)) {
					Exceptions
							.throwRuntime(PlatformException.get(
									RealmError.INVALIDATE_FORM_FIELD, getReferenceKey(realm, destination.getReference())
											+ REFERENCE_DELIMETER + field.getReference().value(),
									realm.getClass().getName()));
				}

				AbstractField src = getField(source, field.getReference());

				// Replace soft reference with actual field object
				it.set(src);

				// Save reference
				saveReferenceMapping(realm, destination.getReference(), field.getReference(), src.getId());
			}
		}
	}

	/**
	 * Gets the field for the reference
	 * 
	 * @param section   The section that contains the field being referenced
	 * @param reference The field reference
	 * @return
	 */
	private AbstractField getField(Section section, Reference reference) {

		assert !(reference instanceof SectionReference);

		String fieldId = getReferenceId(section.getReference().realm(), section.getReference(), reference);

		AbstractField src = section.getField(reference);

		if (!ALLOW_MULTI_REFERENCING) {
			assert src == getFormFieldObjects().get(fieldId);
		}

		return AbstractField.copyOf(src);
	}

	/**
	 * Gets the section referenced by the section reference
	 * 
	 * @param realm      The realm that declared the section reference
	 * @param sReference The section reference
	 * @return
	 */
	private Section getSection(Realm realm, SectionReference sReference) {

		Section section = getFormSectionMap().get(sReference.realm().name()).get(sReference.value());

		if (!ALLOW_MULTI_REFERENCING) {

			String referenceKey = getReferenceKey(sReference.realm(), sReference);
			String sectionId = getReferenceId(referenceKey);

			if (sectionId == null) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_SECTION_REFERENCE, referenceKey,
						realm.getClass().getName()));
			}

			assert section == getFormSectionObjects().get(sectionId);
			assert match(section.getReference().realm(), sReference.realm());

		}

		return section;
	}

	@BlockerTodo("Create a fine grained mechanism to determine order that section is processed")
	private void registerFormSection(Realm realm, Section section) {

		SectionReference sReference = section.getReference();

		// Check if section reference already exists

		String sectionId = getReferenceId(getReferenceKey(realm, sReference));

		if (sectionId != null) {

			// Scenario *1
			// In this scenario, the declared section reference already
			// exists, so we need to copy the declared fields into
			// the existing section

			section.getFields(BaseSimpleField.class).forEach(field -> {
				newField(realm, sReference, field);
			});

			Section existing = getFormSectionMap().get(realm.name()).get(sReference.value());
			existing.withFields(section.getFields());

		} else {

			// Create new section from reference (either hard or soft)
			newSection(realm, section);
		}
	}

	private void removeRealm(Realm realm) {

		if (!realm.enabled()) {
			return;
		}

		List<String> functionalityList = getFunctionalitiesMap().get(realm.name());
		Map<String, Section> sectionsMap = getFormSectionMap().get(realm.name());

		// Check if a role is using any of the functionalities declared in this
		// realm
		for (Functionality functionality : realm.functionalities()) {

			List<String> roles = getReferencePoints(functionality);
			if (!roles.isEmpty()) {
				Exceptions.throwRuntime(PlatformException
						.get(RealmError.REALM_IN_USE_BY_ROLES, functionality.asString(), realm.getClass().getName())
						.setData(roles));
			}
		}

		// Check to see if any of the sections are being referenced by other
		// realms

		List<String> referencesPoints = getReferencePoints(realm);

		if (!referencesPoints.isEmpty()) {
			Exceptions.throwRuntime(PlatformException.get(RealmError.REALM_IN_USE_BY_OTHER_REALMS, realm.name())
					.setData(referencesPoints));
		}

		boolean isBaseRealm = Realm.isBaseRealm(realm);

		if (isBaseRealm) {

			// Check if any other realm definition has added to the functionalities of this
			// realm

			boolean functionalitiesAdded = functionalityList.size() > realm.functionalities().length;
			if (functionalitiesAdded) {
				Exceptions.throwRuntime(
						PlatformException.get(RealmError.BASE_REALM_IN_USE_FUNCTIONALITIES_ADDED, realm.name()));
			}

			// Check if any other realm definition has added to the sections of this realm
			boolean formSectionsAdded = sectionsMap.size() > realm.onboardingForm().length;
			if (formSectionsAdded) {
				Exceptions.throwRuntime(
						PlatformException.get(RealmError.BASE_REALM_IN_USE_FORM_SECTION_ADDED, realm.name()));
			}
		}
		

		// Remove functionalities

		for (Functionality f : realm.functionalities()) {
			functionalityList.remove(f.asString());
		}

		if (functionalityList.isEmpty()) {
			getFunctionalitiesMap().remove(realm.name());
		}

		// Remove sections and fields accordingly

		for (Section section : realm.onboardingForm()) {

			String sectionId = getFormElementMapping().remove(getReferenceKey(realm, section.getReference()));

			assert sectionId.equals(section.getId());
			
			if (getFormSectionObjects().get(sectionId) == section) {
				getFormSectionObjects().remove(sectionId);
			} 

			section.getFields().forEach(field -> {

				String fieldId = getFormElementMapping().remove(getReferenceKey(realm, section.getReference(), field.getReference()));
				
				assert fieldId .equals(field.getId());
				
				if (getFormFieldObjects().get(field.getId()) == field) {
					getFormFieldObjects().remove(field.getId());
				}

			});

			sectionsMap.remove(section.getReference().value());
		}

		if (sectionsMap.isEmpty()) {
			getFormSectionMap().remove(realm.name());
		}

		getLoadedRealmClasses().remove(realm.getClass().getName());

		if (isBaseRealm) {

			assert getFunctionalitiesMap().get(realm.name()) == null;
			assert getFormSectionMap().get(realm.name()) == null;

			getRealmObjectMap().remove(realm.name());
		}

	}

	/**
	 * This scans for role(s) that have the specified functionality added to them
	 * 
	 * @param functionality
	 * @return
	 */
	private List<String> getReferencePoints(Functionality functionality) {

		List<String> roles = new ArrayList<>();

		getRoleFunctionalities().forEach((role, roleFunctionalities) -> {
			if (roleFunctionalities.contains(functionality.asString())) {
				roles.add(role);
			}
		});

		return roles;
	}

	/**
	 * Scans for other realms that depend on the specifies realm and
	 * sectionReference
	 * 
	 * @param realm
	 * @param sectionReference
	 * @return
	 */
	private List<String> getReferencePoints(Realm realm) {

		List<String> points = new ArrayList<>();

		for (Section section : realm.onboardingForm()) {

			String referenceKey = getReferenceKey(realm, section.getReference());

			getLoadedRealmClasses().forEach(realmClass -> {

				Realm r = ClassUtils.createInstance(realmClass);

				for (Section s : r.onboardingForm()) {

					SectionReference sRef = s.getReference();

					if (getReferenceKey(sRef.realm(), sRef).equals(referenceKey)) {
						points.add(realmClass);
					}
				}
			});
		}

		return points;
	}

	private void validateForm(Realm realm) {

		Map<String, List<String>> sections = new HashMap<>();

		for (Section section : realm.onboardingForm()) {

			if (section.getId() != null || section.getReference() == null) {
				Exceptions.throwRuntime(
						PlatformException.get(RealmError.INVALID_FORM_SECTION_IDENTIFIER, realm.getClass().getName()));
			}

			if (sections.containsKey(section.getReference().value())) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_SECTION, section.getId(),
						realm.getClass().getName()));
			}

			List<String> fields = new ArrayList<>();
			sections.put(section.getReference().value(), fields);

			section.getFields().forEach(question -> {

				if (question.getId() != null || question.getReference() == null) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FORM_QUESTION_IDENTIFIER,
							section.getReference().value(), realm.getClass().getName()));
				}

				if (fields.contains(question.getReference().value())) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_QUESTION, question.getId(),
							realm.getClass().getName()));
				}

				fields.add(question.getReference().value());
			});

		}
	}

	@Override
	public List<Functionality> getFunctionalities(Realm realm) {

		if (!getRealmObjectMap().containsKey(realm.name())) {
			return null;
		}

		List<Functionality> result = new ArrayList<>();
		getFunctionalitiesMap().get(realm.name()).forEach(f -> {
			result.add(Functionality.fromString(f));
		});
		return result;
	}

	@Override
	public Map<String, Collection<String>> getAllFunctionalities() {

		Collection<String> names = getRealmNames();
		Map<String, Collection<String>> result = new HashMap<>(names.size());

		names.forEach(n -> {
			result.put(n, getFunctionalitiesMap().get(n));
		});
		return result;
	}

	@SuppressWarnings("unchecked")
	private Map<String, BaseSimpleField> getFormFieldObjects() {
		return (Map<String, BaseSimpleField>) get(FORM_FIELD_OBJECTS_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Section> getFormSectionObjects() {
		return (Map<String, Section>) get(FORM_SECTION_OBJECTS_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getFormElementMapping() {
		return (Map<String, String>) get(FORM_ELEMENT_MAPPING_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getFunctionalitiesMap() {
		return (Map<String, List<String>>) get(FUNCTIONALITIES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Section>> getFormSectionMap() {
		return (Map<String, Map<String, Section>>) get(FORM_SECTION_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getRoleFunctionalities() {
		return (Map<String, List<String>>) get(ROLE_FUNCTIONALITIES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private List<String> getLoadedRealmClasses() {
		return (List<String>) get(LOADED_REALM_CLASSES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Realm> getRealmObjectMap() {
		return (Map<String, Realm>) get(REALM_OBJECT_NAMESPACE);
	}

	private void createResourceMaps() {
		set(FORM_FIELD_OBJECTS_NAMESPACE, new HashMap<>());
		set(FORM_SECTION_OBJECTS_NAMESPACE, new HashMap<>());
		set(FORM_ELEMENT_MAPPING_NAMESPACE, new HashMap<>());
		set(FUNCTIONALITIES_NAMESPACE, new HashMap<>());
		set(FORM_SECTION_NAMESPACE, new HashMap<>());

		set(ROLE_FUNCTIONALITIES_NAMESPACE, new ArrayList<>());
		set(LOADED_REALM_CLASSES_NAMESPACE, new ArrayList<>());
		set(REALM_OBJECT_NAMESPACE, new HashMap<>());
	}

	@Override
	public void removeRoleFunctionalities(String role, Collection<Functionality> functionalities) {
		
		Map<String, List<String>> functionalitiesMap = getRoleFunctionalities();
		List<String> functionalityList = functionalitiesMap.get(role);

		functionalities.forEach(f -> {
			functionalityList.remove(f.asString());
		});
		
	}
	
	@Override
	public void addRoleFunctionalities(String role, Collection<Functionality> functionalities) {

		Map<String, List<String>> functionalitiesMap = getRoleFunctionalities();

		if (!functionalitiesMap.containsKey(role)) {
			functionalitiesMap.put(role, new ArrayList<>());
		}

		List<String> functionalityList = functionalitiesMap.get(role);

		functionalities.forEach(f -> {
			functionalityList.add(f.asString());
		});
	}

	@Override
	public Collection<Functionality> getRoleFunctionalities(String role) {

		Collection<Functionality> result = new ArrayList<>();

		Collection<String> functionalities = getRoleFunctionalities().get(role);

		if (functionalities == null) {
			return null;
		}

		functionalities.forEach(f -> {
			result.add(Functionality.fromString(f));
		});

		return result;
	}
}
