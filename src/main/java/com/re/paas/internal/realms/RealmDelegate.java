package com.re.paas.internal.realms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.infinispan.commons.util.InfinispanCollections;
import org.infinispan.multimap.api.embedded.MultimapCache;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.re.paas.api.Platform;
import com.re.paas.api.annotations.develop.BlockerTodo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.ModifyType;
import com.re.paas.api.classes.PlatformException;
import com.re.paas.api.forms.AbstractField;
import com.re.paas.api.forms.BaseSimpleField;
import com.re.paas.api.forms.CompositeField;
import com.re.paas.api.forms.Reference;
import com.re.paas.api.forms.Reference.RefType;
import com.re.paas.api.forms.Section;
import com.re.paas.api.forms.SectionReference;
import com.re.paas.api.forms.SimpleField;
import com.re.paas.api.fusion.functionalities.Functionality;
import com.re.paas.api.fusion.functionalities.RealmFunctionality;
import com.re.paas.api.fusion.services.ServiceAffinity;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.realms.AbstractRealmDelegate;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.api.realms.RealmSpec;
import com.re.paas.api.runtime.ClassLoaders;
import com.re.paas.api.runtime.spi.DelegateInitResult;
import com.re.paas.api.runtime.spi.DelegateSpec;
import com.re.paas.api.runtime.spi.DistributedStoreConfig;
import com.re.paas.api.runtime.spi.ResourceStatus;
import com.re.paas.api.runtime.spi.SpiType;
import com.re.paas.api.utils.ClassUtils;
import com.re.paas.api.utils.Utils;
import com.re.paas.integrated.models.ModelDelegate;
import com.re.paas.integrated.models.RBModel;
import com.re.paas.internal.classes.ClassUtil;
import com.re.paas.internal.errors.RealmError;
import com.re.paas.internal.fusion.imagineui.UIContext;
import com.re.paas.internal.tasks.TaskDelegate;

@DelegateSpec(dependencies = { SpiType.FUNCTIONALITY })
public class RealmDelegate extends AbstractRealmDelegate {

	private static final Logger LOGGER = Logger.get(RealmDelegate.class);

	private static final String FUNCTIONALITIES_NAMESPACE = "functionalities";
	private static final String FORM_SECTION_NAMESPACE = "form_section";

	private static final String ROLE_FUNCTIONALITIES_NAMESPACE = "roleFunctionalities";
	private static final String REALM_ROLES_NAMESPACE = "realmRoles";

	private static final String LOADED_REALM_CLASSES_NAMESPACE = "loadedClasses";
	private static final String REALM_OBJECT_NAMESPACE = "object";

	// This is used by non-standalone realm classes

	@BlockerTodo("These should be persisted, see validateChanges(...)")
	private static final String FUNCTIONALITIES_REF_NAMESPACE = "functionalities_ref";
	private static final String FORM_REF_NAMESPACE = "form_ref";

	private static final String FUNCTIONALITIES_REF_SIBLINGS_NAMESPACE = "functionalities_ref_sibs";
	private static final String FORM_REF_SIBLINGS_NAMESPACE = "form_ref_sibs";

	private static final Map<String, Object> snapshots = new HashMap<>();

	@Override
	public DelegateInitResult init() {

		createResourceMaps();

		return forEach(this::add);
	}

	@Override
	public List<Object> distributedStoreNames() {
		return Arrays.asList(FUNCTIONALITIES_NAMESPACE, FORM_SECTION_NAMESPACE, ROLE_FUNCTIONALITIES_NAMESPACE,
				REALM_ROLES_NAMESPACE, LOADED_REALM_CLASSES_NAMESPACE, REALM_OBJECT_NAMESPACE,
				new DistributedStoreConfig(FUNCTIONALITIES_REF_NAMESPACE, true),
				new DistributedStoreConfig(FORM_REF_NAMESPACE, true),
				new DistributedStoreConfig(FUNCTIONALITIES_REF_SIBLINGS_NAMESPACE, true),
				new DistributedStoreConfig(FORM_REF_SIBLINGS_NAMESPACE, true));
	}
	

	private void createResourceMaps() {

		getLocalStore().put(FUNCTIONALITIES_NAMESPACE, new HashMap<>());
		getLocalStore().put(FORM_SECTION_NAMESPACE, new HashMap<>());

		getLocalStore().put(FUNCTIONALITIES_REF_NAMESPACE, new HashMap<>());
		getLocalStore().put(FORM_REF_NAMESPACE, new HashMap<>());

		getLocalStore().put(FUNCTIONALITIES_REF_SIBLINGS_NAMESPACE, new HashMap<>());
		getLocalStore().put(FORM_REF_SIBLINGS_NAMESPACE, new HashMap<>());

		getLocalStore().put(ROLE_FUNCTIONALITIES_NAMESPACE, new ArrayList<>());
		getLocalStore().put(REALM_ROLES_NAMESPACE, new ArrayList<>());

		getLocalStore().put(REALM_OBJECT_NAMESPACE, new HashMap<>());
		getLocalStore().put(LOADED_REALM_CLASSES_NAMESPACE, new ArrayList<>());
	}

	

	@SuppressWarnings("unchecked")
	private Map<String, List<Functionality>> getFunctionalitiesMap() {
		return (Map<String, List<Functionality>>) getLocalStore().get(FUNCTIONALITIES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Section>> getFormSectionMap() {
		return (Map<String, Map<String, Section>>) getLocalStore().get(FORM_SECTION_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getRoleFunctionalities() {
		return (Map<String, List<String>>) getLocalStore().get(ROLE_FUNCTIONALITIES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> getRealmRoles() {
		return (Map<String, List<String>>) getLocalStore().get(REALM_ROLES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private List<Class<? extends Realm>> getLoadedRealmClasses() {
		return (List<Class<? extends Realm>>) get(LOADED_REALM_CLASSES_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Realm> getRealmObjectMap() {
		return (Map<String, Realm>) getLocalStore().get(REALM_OBJECT_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Class<? extends Realm>>> getFunctionalitiesRefs() {
		return (Map<String, Map<String, Class<? extends Realm>>>) getLocalStore().get(FUNCTIONALITIES_REF_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Class<? extends Realm>>> getFormRefs() {
		return (Map<String, Map<String, Class<? extends Realm>>>) getLocalStore().get(FORM_REF_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, List<Class<? extends Realm>>>> getFunctionalitiesSiblingsRefs() {
		return (Map<String, Map<String, List<Class<? extends Realm>>>>) getLocalStore()
				.get(FUNCTIONALITIES_REF_SIBLINGS_NAMESPACE);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, List<Class<? extends Realm>>>> getFormSiblingsRefs() {
		return (Map<String, Map<String, List<Class<? extends Realm>>>>) getLocalStore()
				.get(FORM_REF_SIBLINGS_NAMESPACE);
	}
	
	

	private AbstractField getFieldSnapshot(String realmName, String sRef, String fRef) {
		return (AbstractField) snapshots.get(realmName + "/" + sRef + "/" + fRef);
	}

	private AbstractField saveFieldSnapshot(String realmName, String sRef, AbstractField field) {
		return (AbstractField) snapshots.put(realmName + "/" + sRef + "/" + field.getReference().asString(), field);
	}

	private Section getSectionSnapshot(String realmName, String sRef) {
		return (Section) snapshots.get(realmName + "/" + sRef);
	}

	private Section saveSectionSnapshot(String realmName, Section section) {
		return (Section) snapshots.put(realmName + "/" + section.getReference().asString(), section);
	}

	@Override
	protected Collection<?> getResourceObjects() {
		return getLoadedRealmClasses();
	}

	@Override
	protected ResourceStatus add(Class<Realm> clazz) {
		Integer i = add0(clazz);
		return i > 0 ? ResourceStatus.NOT_UPDATED.setCount(i) : ResourceStatus.UPDATED;
	}

	/**
	 * We cannot register standalone realms at a later time after platform has
	 * started, because by then the parent realm may have been updated by
	 * non-standalone realms
	 */
	@Override
	protected Boolean canRegisterInPlace(Class<Realm> clazz) {
		return getParent(clazz).equals(Realm.class) || !isStandalone(clazz);
	}

	@SuppressWarnings("incomplete-switch")
	private void registerSections(Realm realm, Predicate<Section> sectionFilter, Predicate<AbstractField> fieldFilter) {

		RealmSpec spec = realm.getClass().getAnnotation(RealmSpec.class);
		Realm parent = (spec != null && !Objects.equals(spec.parent(), Realm.class))
				? ClassUtils.createInstance(spec.parent())
				: null;
		Boolean standalone = spec != null ? spec.standalone() : true;

		Map<String, Section> sectionMap = null;

		if (standalone) {

			if (parent == null) {
				sectionMap = new HashMap<>();

			} else {

				sectionMap = getFormSectionMap().get(parent.name());

				Map<String, Section> m = new HashMap<>(sectionMap.size());

				sectionMap.forEach((k, v) -> {
					m.put(v.getReference().asString(), v.deepClone());
				});

				sectionMap = m;
			}

			getFormSectionMap().put(realm.name(), sectionMap);

		} else {

			assert realm.name().equals(parent.name());

			sectionMap = getFormSectionMap().get(parent.name());
		}

		for (Section s : realm.onboardingForm()) {

			if (!sectionFilter.test(s)) {
				continue;
			}

			SectionReference sReference = s.getReference();
			Boolean updateSection = true;

			if ((!standalone)
					&& !getFormRefs().get(parent.name()).get(sReference.asString()).equals(realm.getClass())) {
				updateSection = false;
			}

			switch (sReference.getModifyType()) {

			case ADD:

				assert !sectionMap.containsKey(sReference.asString());

				// In the scenario where sections are added, we never store refs (for
				// non-standalone) for those because there is guaranteed never to be a clash,
				// since sections with modify type == ADD must be relative to the declaring
				// realm class

				sectionMap.put(sReference.asString(), s);
				break;

			case REMOVE:

				if (updateSection) {

					if (!sectionMap.containsKey(sReference.asString())) {

						// A non-standalone sibling realm must have already removed this section
						assert (!standalone) && getSectionSnapshot(parent.name(), sReference.asString()) != null;

						break;
					}

					Section section = sectionMap.remove(sReference.asString());

					if (!standalone) {

						Section snapshot = getSectionSnapshot(parent.name(), sReference.asString());

						if (snapshot == null) {

							// Add snapshot, since no update has happened on the section

							saveSectionSnapshot(parent.name(), section);

						} else {

							// It means that an update has happened on this section before now,
							// we don't want to add snapshot to avoid overwriting the original
							// one added before the update happened
						}
					}
				}

				break;

			case UPDATE:

				if (updateSection && !sectionMap.containsKey(sReference.asString())) {

					// A sibling likely removed the section, but in any case a snapshot must exist
					Section snapshot = getSectionSnapshot(parent.name(), sReference.asString());

					assert (!standalone) && snapshot != null;

					sectionMap.put(sReference.asString(), snapshot.deepClone().importData(s, false, false));

					// Since <sectionMap> has been updated here, there is no need to update section
					// below
					updateSection = false;
				}

				Iterator<Section> i = sectionMap.values().iterator();

				while (i.hasNext()) {

					Section section = i.next();

					if (!section.getReference().asString().equals(sReference.asString())) {
						continue;
					}

					if (updateSection) {

						if (!standalone) {

							Section snapshot = getSectionSnapshot(parent.name(), sReference.asString());

							if (snapshot == null) {

								// This is the first non-standalone realm to update this section
								// It's imperative that we clone because changes will soon
								// be made on the <section> instance

								saveSectionSnapshot(parent.name(), section.clone());

							} else {

								// Another non-standalone sibling realm updated this section

								// So, we need to merge the changes from the
								// snapshot into the existing <section> instance
								section.importData(snapshot, false, false);
							}
						}

						// Update section info
						section.importData(s, false, false);
					}

					// Update fields
					s.getFields().stream().filter(fieldFilter).forEach(f -> {

						Reference fReference = f.getReference();

						String canonicalRef = sReference.asString() + "/" + fReference.asString();

						if ((!standalone)
								&& !getFormRefs().get(parent.name()).get(canonicalRef).equals(realm.getClass())) {
							return;
						}

						Iterator<AbstractField> j = section.getFields().iterator();

						boolean fieldFound = false;

						while (j.hasNext()) {

							AbstractField field = j.next();

							if (!field.getReference().asString().equals(fReference.asString())) {
								continue;
							}

							fieldFound = true;

							if (fReference.getModifyType() == ModifyType.ADD) {

								// This is only possible if sibling already added this field, handle below,
								// after break
								assert !standalone;

								break;
							}

							switch (f.getReference().getModifyType()) {
							case REMOVE:

								if (!standalone) {

									AbstractField snapshot = getFieldSnapshot(parent.name(), sReference.asString(),
											fReference.asString());

									if (snapshot == null) {

										// Add snapshot, since no update has happened on the field
										saveFieldSnapshot(parent.name(), sReference.asString(), field);

									} else {

										// It means that an update has happened on this field before now,
										// we don't want to add snapshot to avoid overwriting the original
										// one added before the update happened
									}

								}

								j.remove();
								break;

							case UPDATE:

								if (!standalone) {

									AbstractField snapshot = getFieldSnapshot(parent.name(), sReference.asString(),
											fReference.asString());

									if (snapshot == null) {

										// This is the first non-standalone realm to update this field
										saveFieldSnapshot(parent.name(), sReference.asString(), field.clone());

									} else {

										// Another non-standalone sibling realm updated this field
										field.importData(snapshot, false);
									}
								}

								field.importData(f, false);
								break;
							}

							// Break while loop, since field has been found
							break;
						}

						if (!fieldFound) {

							switch (f.getReference().getModifyType()) {

							case ADD:

								section.getFields().add(f);
								break;

							case REMOVE:

								assert (!standalone) && getFieldSnapshot(parent.name(), sReference.asString(),
										fReference.asString()) != null;

								// The field was already removed by a non-standalone sibling
								// So, do nothing

								break;

							case UPDATE:

								AbstractField snapshot = getFieldSnapshot(parent.name(), sReference.asString(),
										fReference.asString());

								assert (!standalone) && snapshot != null;

								// The field was already removed by a non-standalone sibling
								// So, first create a clone of the existing snapshot (so we don't override it's
								// data), merge the current changes
								// into the existing snapshot, and re-add

								section.getFields().add(snapshot.clone().importData(f, false));

								break;
							}

						} else {

							if (f.getReference().getModifyType() == ModifyType.ADD) {

								assert !standalone;

								// We want to add a field, but a sibling added a field with the same
								// ref already. Remove the added field, and put this one instead

								section.removeField(f.getReference());
								section.getFields().add(f);
							}
						}
					});
				}

				break;
			}
		}
	}

	private void registerFunctionalities(Realm realm, Predicate<Functionality> filter) {

		RealmSpec spec = realm.getClass().getAnnotation(RealmSpec.class);
		Realm parent = (spec != null && !Objects.equals(spec.parent(), Realm.class))
				? ClassUtils.createInstance(spec.parent())
				: null;
		Boolean standalone = spec != null ? spec.standalone() : true;

		List<Functionality> functionalities = null;

		if (standalone) {

			if (parent == null) {
				functionalities = new ArrayList<>();
			} else {
				functionalities = getFunctionalitiesMap().get(parent.name());
				List<Functionality> f = new ArrayList<>(functionalities.size());
				Collections.copy(f, functionalities);
				functionalities = f;
			}

			getFunctionalitiesMap().put(realm.name(), functionalities);

		} else {
			functionalities = getFunctionalitiesMap().get(parent.name());
		}

		for (RealmFunctionality f : realm.functionalities()) {

			if (!filter.test(f.getFunctionality())) {
				continue;
			}

			registerFunctionality(realm, functionalities, f);
		}
	}

	private void registerFunctionality(Realm realm, List<Functionality> functionalities, RealmFunctionality f) {

		List<String> functionalitiesList = functionalities.stream().map(f0 -> f0.asString())
				.collect(Collectors.toList());

		Boolean standalone = isStandalone(realm.getClass());

		Boolean updateFunctionality = true;

		if ((!standalone) && !getFunctionalitiesRefs().get(realm.name()).get(f.asString()).equals(realm.getClass())) {
			updateFunctionality = false;
		}

		if (!updateFunctionality) {
			return;
		}

		switch (f.getModifyType()) {
		case ADD:

			if (functionalitiesList.contains(f.asString())) {

				// This was added by a sibling
				assert !standalone;

			} else {
				functionalities.add(f.getFunctionality());
			}

			break;

		case REMOVE:

			if (!functionalitiesList.contains(f.asString())) {

				// This was removed by a sibling
				assert !standalone;

			} else {

				if (!standalone) {

					List<String> rolesInUse = getReferencePoints(realm, f.getFunctionality());

					if (!rolesInUse.isEmpty()) {

						Exceptions.throwRuntime(PlatformException
								.get(RealmError.FUNCTIONALITY_IN_USE_BY_ROLES, f.asString(), realm.getClass().getName())
								.setData(rolesInUse));
					}

				} else {

					// If this is a standalone realm, then no role can possibly be using it
					// Ensure that no role in the specified realm is currently using this
					// functionality
				}

				Iterator<Functionality> it = functionalities.iterator();

				while (it.hasNext()) {

					Functionality f0 = it.next();
					if (f.asString().equals(f0.asString())) {
						it.remove();
					}
				}
			}

			break;

		case UPDATE:
			break;
		}
	}

	/**
	 * This scans for role(s) that have the specified functionality added to them
	 * 
	 * @param functionality
	 * @return
	 */
	private List<String> getReferencePoints(Realm realm, Functionality functionality) {

		List<String> roles = getRoles(realm.name());

		getRoleFunctionalities().entrySet().stream().filter(e -> roles.contains(e.getKey()))
				.filter(e -> e.getValue().contains(functionality.asString())).map(e -> e.getKey())
				.collect(Collectors.toList());

		return roles;
	}

	/**
	 * We need to properly sort the realm classes, such that standalone realms come
	 * first before their non-standalone counterpart. This is because we want
	 * standalone realms to copy "pure" data that has not yet been overridden by any
	 * changes declared in non-standalone realms
	 */
	@Override
	public Comparator<Class<? extends Realm>> getClassComparator() {

		return new Comparator<Class<? extends Realm>>() {
			@Override
			public int compare(Class<? extends Realm> o1, Class<? extends Realm> o2) {

				if (isStandalone(o1)) {

					if (isStandalone(o2)) {
						return 0;
					} else {
						return -1;
					}

				} else {

					if (!isStandalone(o2)) {
						return 0;
					} else {
						return 1;
					}
				}
			}
		};

		// a negative integer, zero, or a positive integer as the first argument is less
		// than, equal to, or greater than the second.

	}

	private Boolean addRealm(Realm realm, Realm parent, Boolean standalone) {

		if (getLoadedRealmClasses().contains(realm.getClass())) {

			// This class has already been processed
			return false;
		}

		// Validate realm definition
		this.validateDefinition(realm);

		if (standalone) {

			getRealmObjectMap().put(realm.name(), realm);

		} else {

			// validate realm changes
			// note: we are doing this to avoid clashing changes among non-standalone
			// realms that have the same parent

			// Check startup context

			// Is App being installed ?

			this.validateChanges(realm, s -> true, f -> true, f -> true);
		}

		// Register sections
		this.registerSections(realm, s -> true, f -> true);

		// Register functionalities
		this.registerFunctionalities(realm, f -> true);

		// Indicate that the realm class has been processed
		getLoadedRealmClasses().add(realm.getClass());

		return standalone;
	}

	private Integer add0(Class<Realm> clazz) {

		Integer i = 0;

		RealmSpec spec = validateRealmSpec(clazz);
		Boolean hasParent = spec != null && !Objects.equals(spec.parent(), Realm.class);

		if (hasParent) {

			// Load realm class
			i += add0(spec.parent());
		}

		Realm realm = ClassUtil.createInstance(clazz);

		Boolean standalone = spec != null ? spec.standalone() : true;

		Realm parent = (spec != null && !Objects.equals(spec.parent(), Realm.class))
				? ClassUtils.createInstance(spec.parent())
				: null;

		i += addRealm(realm, parent, standalone) ? 1 : 0;

		return i;
	}

	@Override
	protected ResourceStatus remove(Class<Realm> clazz) {
		try {
			Integer i = removeRealm(clazz);
			return i > 0 ? ResourceStatus.UPDATED.setCount(i) : ResourceStatus.NOT_UPDATED;

		} catch (Exception e) {
			return ResourceStatus.ERROR;
		}
	}

	@Override
	public Collection<String> getRealmNames() {
		return getRealmObjectMap().keySet();
	}

	private static Section getSection(Section[] values, SectionReference sRef) {
		for (Section s : values) {
			if (s.getReference().asString().equals(sRef.asString())) {
				return s;
			}
		}
		return null;
	}

	private static RealmFunctionality getFunctionality(RealmFunctionality[] values, String fString) {
		for (RealmFunctionality f : values) {
			if (f.asString().equals(fString)) {
				return f;
			}
		}
		return null;
	}

	private Realm getRealm0(String name) {

		Realm r = getRealmObjectMap().get(name);

		return new Realm() {

			@Override
			public Section[] onboardingForm() {
				Collection<Section> values = getFormSectionMap().get(r.name()).entrySet().stream()
						.map(e -> e.getValue()).collect(Collectors.toUnmodifiableList());
				return values.toArray(new Section[values.size()]);
			}

			@Override
			public String name() {
				return r.name();
			}

			@Override
			public RealmFunctionality[] functionalities() {

				List<RealmFunctionality> l = getFunctionalities(r).stream().map(f -> f.forRealm())
						.collect(Collectors.toUnmodifiableList());

				return l.toArray(new RealmFunctionality[l.size()]);
			}

			@Override
			public RealmApplicationSpec applicationSpec() {
				return r.applicationSpec();
			}
		};
	}

	@Override
	public Realm getRealm(String name) {
		return getRealm0(name);
	}

	@Override
	public List<Functionality> getFunctionalities(Realm realm) {
		return getFunctionalitiesMap().get(realm.name());
	}

	private RealmSpec validateRealmSpec(Class<Realm> clazz) {

		RealmSpec spec = clazz.getAnnotation(RealmSpec.class);

		if (spec == null) {
			return null;
		}

		Realm realm = ClassUtil.createInstance(clazz);

		if (!spec.standalone()) {

			if (spec.parent().equals(Realm.class)) {
				// Must have a parent
				Exceptions.throwRuntime("Non-standalone realm: " + clazz.getName() + " must`` have a parent realm");
			}

			Realm parent = ClassUtil.createInstance(spec.parent());

			// realm names must match
			if (!realm.name().equals(parent.name())) {
				Exceptions.throwRuntime(
						"Non-standalone realm: " + clazz.getName() + " must have the name: " + parent.name());
			}

			// We want to ensure that the parent realm is stand-alone
			RealmSpec parentSpec = spec.parent().getAnnotation(RealmSpec.class);

			if (parentSpec != null && !parentSpec.standalone()) {
				Exceptions.throwRuntime(
						"Realm: " + clazz.getName() + " extends a non-standalone realm: " + spec.parent().getName());
			}

		} else {

			if (!spec.parent().equals(Realm.class)) {

				Realm parent = ClassUtil.createInstance(spec.parent());

				// realm names must not match
				if (realm.name().equals(parent.name())) {
					Exceptions.throwRuntime(
							"Standalone realm: " + clazz.getName() + " must not have the name: " + parent.name());
				}
			}
		}

		return spec;
	}

//	private static boolean isParentRealm(Class<? extends Realm> realmClass, Class<? extends Realm> parentClass) {
//
//		if (realmClass.equals(parentClass)) {
//			return true;
//		}
//
//		RealmSpec spec = realmClass.getAnnotation(RealmSpec.class);
//
//		Class<? extends Realm> parent = (spec != null && !Objects.equals(spec.parent(), Realm.class)) ? spec.parent()
//				: null;
//
//		return parent != null ? isParentRealm(parent, parentClass) : false;
//	}

	@BlockerTodo("Find strategy to save and load settings")
	private void validateChanges(Realm realm, Boolean noSectionOverride, Predicate<Section> sectionFilter,
			Predicate<AbstractField> fieldFilter, Predicate<Functionality> functionalityFilter) {

		String appId = ClassLoaders.getId(realm.getClass());

		Map<String, Class<? extends Realm>> formRefs = getFormRefs().get(realm.name());
		Map<String, List<Class<? extends Realm>>> formSiblingRefs = getFormSiblingsRefs().get(realm.name());

		if (formRefs == null) {
			formRefs = new HashMap<>();
			getFormRefs().put(realm.name(), formRefs);

			formSiblingRefs = new HashMap<>();
			getFormSiblingsRefs().put(realm.name(), formSiblingRefs);
		}

		for (Section section : realm.onboardingForm()) {

			if (!sectionFilter.test(section)) {
				continue;
			}

			if (section.getReference().getModifyType() == ModifyType.ADD) {

				// There is no need to keep track of refs, since the realm on the section ref
				// will always be unique to the declaring realm
				continue;
			}

			String sRef = section.getReference().asString();

			if (section.getTitle() != null || section.getSummary() != null
					|| section.getReference().getModifyType() == ModifyType.REMOVE) {

				Boolean addRef = !formRefs.containsKey(sRef);

				if (!addRef) {

					// Let's ask, inorder to ascertain the user's preference

					String existingApp = ClassLoaders.getId(formRefs.get(sRef));

					if (existingApp.equals(appId)) {
						addRef = true;
					} else {

						Map<String, Object> variables = FluentHashMap.forValueMap().with("existingApp", existingApp)
								.with("newApp", appId).with("section", section.getReference().value())
								.with("realm", section.getReference().realm().name());

						addRef = UIContext.confirm("changes.confirm",
								RBModel.get("changes.conflicting.form_section", variables));
					}
				}

				if (addRef) {
					formRefs.put(sRef, realm.getClass());
				}

				List<Class<? extends Realm>> refs = formSiblingRefs.get(sRef);

				if (refs == null) {
					refs = new ArrayList<>();
					formSiblingRefs.put(sRef, refs);
				}

				refs.add(realm.getClass());

			}

			for (AbstractField f : section.getFields().stream().filter(fieldFilter)
					.collect(Collectors.toUnmodifiableList())) {

				String fRef = f.getReference().asString();

				String canonicalRef = sRef + "/" + fRef;

				Boolean addRef = !formRefs.containsKey(canonicalRef);

				if (!addRef) {

					// Let's ask, inorder to ascertain the user's preference

					String existingApp = ClassLoaders.getId(formRefs.get(canonicalRef));

					if (existingApp.equals(appId)) {
						addRef = true;
					} else {

						Map<String, Object> variables = FluentHashMap.forValueMap().with("existingApp", existingApp)
								.with("newApp", appId).with("field", fRef)
								.with("section", section.getReference().value())
								.with("realm", section.getReference().realm().name());

						addRef = UIContext.confirm("changes.confirm",
								RBModel.get("changes.conflicting.form_field", variables));
					}
				}

				if (addRef) {
					formRefs.put(canonicalRef, realm.getClass());
				}

				List<Class<? extends Realm>> refs = formSiblingRefs.get(canonicalRef);

				if (refs == null) {
					refs = new ArrayList<>();
					formSiblingRefs.put(canonicalRef, refs);
				}

				refs.add(realm.getClass());
			}
		}

		Map<String, Class<? extends Realm>> functionalityRefs = getFunctionalitiesRefs().get(realm.name());
		Map<String, List<Class<? extends Realm>>> functionalitySiblingRefs = getFunctionalitiesSiblingsRefs()
				.get(realm.name());

		if (functionalityRefs == null) {

			functionalityRefs = new HashMap<>();
			getFunctionalitiesRefs().put(realm.name(), functionalityRefs);

			functionalitySiblingRefs = new HashMap<>();
			getFunctionalitiesSiblingsRefs().put(realm.name(), functionalitySiblingRefs);
		}

		for (RealmFunctionality f : realm.functionalities()) {

			if (!functionalityFilter.test(f.getFunctionality())) {
				continue;
			}

			String fString = f.asString();

			Boolean addRef = !functionalityRefs.containsKey(fString);

			if (!addRef) {

				// Let's ask, inorder to ascertain the user's preference

				String existingApp = ClassLoaders.getId(functionalityRefs.get(fString));
				String newApp = ClassLoaders.getId(realm.getClass());

				if (existingApp.equals(newApp)) {
					addRef = true;
				} else {

					Map<String, Object> variables = FluentHashMap.forValueMap().with("existingApp", existingApp)
							.with("newApp", newApp).with("functionality", f.getFunctionality().getName());

					addRef = UIContext.confirm("changes.confirm",
							RBModel.get("changes.conflicting.functionality", variables));
				}
			}

			if (addRef) {
				functionalityRefs.put(fString, realm.getClass());
			}

			List<Class<? extends Realm>> refs = functionalitySiblingRefs.get(fString);

			if (refs == null) {
				refs = new ArrayList<>();
				functionalitySiblingRefs.put(fString, refs);
			}

			refs.add(realm.getClass());
		}
	}

	private static Boolean isStandalone(Class<? extends Realm> clazz) {

		RealmSpec spec = clazz.getAnnotation(RealmSpec.class);

		return spec != null ? spec.standalone() : true;
	}

	private static Class<? extends Realm> getParent(Class<? extends Realm> clazz) {

		RealmSpec spec = clazz.getAnnotation(RealmSpec.class);

		Class<Realm> parent = (spec != null && !Objects.equals(spec.parent(), Realm.class)) ? spec.parent()
				: Realm.class;

		return parent;
	}

	private static List<Class<? extends Realm>> getParents(Class<? extends Realm> clazz) {

		List<Class<? extends Realm>> parents = new ArrayList<>();

		Class<? extends Realm> parent = getParent(clazz);

		while (!parent.equals(Realm.class)) {
			parents.add(parent);
			parent = getParent(parent);
		}

		return parents;
	}

//	private List<Class<? extends Realm>> getDirectChildren(Class<? extends Realm> realmClass) {
//		return getLoadedRealmClasses().stream().filter(clazz -> getParent(clazz).equals(realmClass))
//				.collect(Collectors.toUnmodifiableList());
//	}
//
//	private List<Class<? extends Realm>> getSiblings(Class<? extends Realm> realmClass) {
//
//		List<Class<? extends Realm>> siblings = new ArrayList<>();
//		Class<? extends Realm> parent = getParent(realmClass);
//
//		getLoadedRealmClasses().forEach(clazz -> {
//
//			if ((!isStandalone(clazz)) && getParent(clazz).equals(parent)) {
//				siblings.add(clazz);
//			}
//		});
//
//		return siblings;
//	}

	private void validateDefinition(Realm realm) {

		RealmSpec spec = realm.getClass().getAnnotation(RealmSpec.class);

		Realm parent = (spec != null && !Objects.equals(spec.parent(), Realm.class))
				? ClassUtils.createInstance(spec.parent())
				: null;

		Boolean standalone = spec != null ? spec.standalone() : true;

		if (standalone) {

			if (getRealmObjectMap().containsKey(realm.name())) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.REALM_NAME_ALREADY_EXISTS, realm.name(),
						realm.getClass().getName()));
			}

		} else {

			// The realm name should match that of it's parent
			if (!realm.name().equals(parent.name())) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.REALM_NAME_INVALID, realm.getClass().getName(),
						parent.name()));
			}
		}

		Map<String, Map<String, AbstractField>> sections = new HashMap<>();

		// First, validate forms sections in on-boarding form

		for (Section section : realm.onboardingForm()) {

			SectionReference sReference = section.getReference();

			if (

			// Ids must not be manually assigned
			section.getId() != null

					// reference must be specified
					|| sReference == null) {

				Exceptions.throwRuntime(
						PlatformException.get(RealmError.INVALID_FORM_SECTION_IDENTIFIER, realm.getClass().getName()));
			}

			if (sReference.realm() == null || sReference.getModifyType() == null || sReference.value() == null
					|| sReference.value().equals("")) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FORM_SECTION_REFERENCE,
						sReference.asString(), realm.getClass().getName()));
			}

			// Reference must be unique
			if (sections.containsKey(sReference.asString())) {

				Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_SECTION, section.getId(),
						realm.getClass().getName()));
			}

			final Predicate<Section> validateSection = (s) -> {

				if ((sReference.getModifyType() == ModifyType.ADD || sReference.getModifyType() == ModifyType.UPDATE)
						&& s.getFields().isEmpty()) {
					return false;
				}

				if (sReference.getModifyType() == ModifyType.REMOVE && !s.getFields().isEmpty()) {
					return false;
				}

				if (standalone && parent == null && s.getReference().getModifyType() != ModifyType.ADD) {
					return false;
				}

				if (s.getReference().getModifyType() == ModifyType.ADD) {
					return s.getReference().realm().getClass().equals(realm.getClass());
				} else {

					return Utils.call(() -> {

						List<Class<? extends Realm>> parents = getParents(realm.getClass());

						for (Class<? extends Realm> c : parents) {

							Realm r = ClassUtils.createInstance(c);
							Section s0 = getSection(r.onboardingForm(), sReference);

							if (s0 != null) {

								switch (s0.getReference().getModifyType()) {

								case ADD:
									return true;

								case REMOVE:
									return false;

								case UPDATE:
									break;
								}
							}
						}

						return false;
					});
				}
			};

			if (!validateSection.test(section)) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_SECTION_DEFINITION, sReference,
						realm.getClass().getName()));
			}

			Map<String, AbstractField> fields = new HashMap<>();

			section.getFields().forEach(field -> {

				Reference fReference = field.getReference();
				String fullyQualifiedReference = section.getReference().asString() + "/" + fReference.asString();

				if (
				// Ids must not be manually assigned
				field.getId() != null

						// reference must be specified
						|| fReference == null) {

					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FORM_FIELD_IDENTIFIER,
							fullyQualifiedReference, realm.getClass().getName()));
				}

				if (fReference == null || fReference.getModifyType() == null || fReference.value() == null
						|| fReference.value().equals("")) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FORM_FIELD_REFERENCE,
							fReference.asString(), realm.getClass().getName()));
				}

				// Reference must be unique
				if (fields.containsKey(fReference.value())) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FORM_FIELD,
							fullyQualifiedReference, realm.getClass().getName()));
				}

				// If sReference.getModifyType() == ModifyType.ADD, fReference.getModifyType()
				// should also be ModifyType.ADD

				if (sReference.getModifyType() == ModifyType.ADD && fReference.getModifyType() != ModifyType.ADD) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALIDATE_FORM_FIELD_MODIFY_TYPE,
							fullyQualifiedReference, realm.getClass().getName()));
				}

				// If sReference.getModifyType() == ModifyType.REMOVE, no field(s) should exist
				// for the given section

				if (sReference.getModifyType() == ModifyType.REMOVE) {
					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALIDATE_FORM_FIELD,
							fullyQualifiedReference, realm.getClass().getName()));
				}

				// If fReference.getModifyType() == ModifyType.ADD, verify that the barest
				// minimum data is provided

				if (fReference.getModifyType() == ModifyType.ADD) {

					Boolean fieldValid = true;

					if (((BaseSimpleField) field).getTitle() == null) {
						fieldValid = false;
					}

					if (field instanceof SimpleField && ((SimpleField) field).getInputType() == null) {
						fieldValid = false;
					}

					if (field instanceof CompositeField) {
						CompositeField composite = (CompositeField) field;
						if (composite.getItemsSource() == null && composite.getItems().isEmpty()) {
							fieldValid = false;
						}
					}

					if (!fieldValid) {
						Exceptions.throwRuntime(PlatformException.get(RealmError.INVALIDATE_FORM_FIELD,
								fullyQualifiedReference, realm.getClass().getName()));
					}
				}

				fields.put(field.getReference().value(), field);
			});

			Predicate<AbstractField> validateField = (f) -> {

				List<Class<? extends Realm>> parents = getParents(realm.getClass());

				for (Class<? extends Realm> c : parents) {

					Realm r = ClassUtils.createInstance(c);
					Section s0 = getSection(r.onboardingForm(), sReference);

					if (s0 != null) {

						AbstractField f0 = s0.getField(f.getReference());

						if (f0 != null) {

							if (f.getReference().getModifyType() == ModifyType.ADD) {

								// The current field cannot be added, since it already
								// exists in a parent
								return false;
							}

							switch (f0.getReference().getModifyType()) {

							case ADD:
								return true;

							case REMOVE:
								return false;

							case UPDATE:
								break;
							}

							break;
						}
					}
				}

				return f.getReference().getModifyType() == ModifyType.ADD;
			};

			fields.forEach((k, v) -> {

				if (!validateField.test(v)) {

					String fullyQualifiedReference = section.getReference().asString() + "/"
							+ v.getReference().asString();

					Exceptions.throwRuntime(PlatformException.get(RealmError.INVALIDATE_FORM_FIELD,
							fullyQualifiedReference, realm.getClass().getName()));
				}
			});

			sections.put(sReference.asString(), fields);
		}

		// Then, validate functionalities

		Map<String, RealmFunctionality> functionalities = new HashMap<>();

		for (RealmFunctionality f : realm.functionalities()) {

			if (f.getModifyType() == ModifyType.UPDATE
					|| (standalone && parent == null && f.getModifyType() != ModifyType.ADD)) {

				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FUNCTIONALITY, f.asString(),
						realm.getClass().getName()));
			}

			// Functionality must be unique
			if (functionalities.containsKey(f.asString())) {

				Exceptions.throwRuntime(PlatformException.get(RealmError.DUPLICATE_FUNCTIONALITY, f.asString(),
						realm.getClass().getName()));
			}

			@SuppressWarnings("incomplete-switch")
			Callable<Boolean> validateFunctionality = () -> {

				List<Class<? extends Realm>> parents = getParents(realm.getClass());

				for (Class<? extends Realm> c : parents) {

					Realm r = ClassUtils.createInstance(c);
					RealmFunctionality f0 = getFunctionality(r.functionalities(), f.asString());

					if (f0 != null) {

						switch (f0.getModifyType()) {

						case ADD:
							return f.getModifyType() == ModifyType.REMOVE;

						case REMOVE:
							return f.getModifyType() == ModifyType.ADD;
						}
					}
				}

				return f.getModifyType() == ModifyType.ADD;
			};

			if (!Utils.call(validateFunctionality)) {
				Exceptions.throwRuntime(PlatformException.get(RealmError.INVALID_FUNCTIONALITY, f.asString(),
						realm.getClass().getName()));
			}

			functionalities.put(f.asString(), f);
		}
	}

	private void canRemoveRealm(Realm realm) {

		Boolean standalone = isStandalone(realm.getClass());

		// Ensure that no functionalities currently in use will be removed

		for (RealmFunctionality f : realm.functionalities()) {

			Boolean functionalityApplies = standalone
					|| getFunctionalitiesRefs().get(realm.name()).get(f.asString()).equals(realm.getClass());

			if (!functionalityApplies) {
				continue;
			}

			if (f.getModifyType() == ModifyType.ADD) {

				List<String> referencePoints = getReferencePoints(realm, f.getFunctionality());

				if (!referencePoints.isEmpty()) {

					Exceptions.throwRuntime(PlatformException
							.get(RealmError.FUNCTIONALITY_IN_USE_BY_ROLES, f.asString(), realm.getClass().getName())
							.setData(referencePoints));
				}
			}
		}
	}

	private void refreshRef(Realm realm, RefType refType, String ref) {

		Map<String, Class<? extends Realm>> formRefs = getFormRefs().get(realm.name());
		formRefs.remove(ref);

		List<Class<? extends Realm>> refs = getFormSiblingsRefs().get(realm.name()).get(ref);
		refs.remove(realm.getClass());
		

		if (!refs.isEmpty()) {

			// Get the replacement realm from the bottom of the ref List
			Class<? extends Realm> c = refs.remove(refs.size() - 1);
			Realm replacement = ClassUtils.createInstance(c);

			switch (refType) {

			}

			if (refType == RefType.SECTION) {

				Predicate<Section> sFilter = s -> s.getReference().asString().equals(ref);

				// register section

				validateChanges(replacement, sFilter, f -> false, f -> false);

				registerSections(replacement, sFilter, f -> false);

			} else {

				List<String> parts = Utils.asList(Splitter.on('/').split(ref));

				String fRef = parts.remove(parts.size() - 1);
				String sRef = Joiner.on('/').join(parts);

				Predicate<Section> sFilter = s -> s.getReference().asString().equals(sRef);
				Predicate<AbstractField> fFilter = f -> f.getReference().asString().equals(fRef);

				// register field

				validateChanges(replacement, sFilter, fFilter, f -> false);

				registerSections(replacement, sFilter, fFilter);
			}

		} else {
			getFormSiblingsRefs().get(realm.name()).remove(ref);
		}
	}

	private void refreshRef(Realm realm, Functionality f) {

		Map<String, Class<? extends Realm>> functionalityRefs = getFunctionalitiesRefs().get(realm.name());
		functionalityRefs.remove(f.asString());

		List<Class<? extends Realm>> refs = getFunctionalitiesSiblingsRefs().get(realm.name()).get(f.asString());
		refs.remove(realm.getClass());

		if (!refs.isEmpty()) {

			// Get the replacement realm from the bottom of the ref List
			Class<? extends Realm> c = refs.remove(refs.size() - 1);
			Realm replacement = ClassUtils.createInstance(c);

			registerFunctionalities(replacement, f0 -> f0.asString().equals(f.asString()));

		} else {
			getFunctionalitiesSiblingsRefs().get(realm.name()).remove(f.asString());
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void removeRealm(Class<? extends Realm> clazz) {

		assert getLoadedRealmClasses().contains(clazz);

		Boolean standalone = isStandalone(clazz);

		Realm realm = ClassUtils.createInstance(clazz);

		// Determine whether realm can be unregistered
		this.canRemoveRealm(realm);

		if (standalone) {

			// Ensure that no role(s) have been created off the specified realm
			List<String> rolesInUse = getRoles(realm.name());

			if (!rolesInUse.isEmpty()) {

				Exceptions.throwRuntime(
						PlatformException.get(RealmError.REALM_IN_USE_BY_ROLES, realm.name()).setData(rolesInUse));
			}

			getFunctionalitiesMap().remove(realm.name());

			// Invoke form hook

			getFormSectionMap().remove(realm.name());

			getRealmObjectMap().remove(realm.name());

			// Also remove refs collections

		} else {

			for (Section s : realm.onboardingForm()) {

				SectionReference sReference = s.getReference();

				Boolean sectionApplies = getFormRefs().get(realm.name()).get(sReference.asString())
						.equals(realm.getClass());

				switch (sReference.getModifyType()) {

				case ADD:

					// Invoke form hook

					getFormSectionMap().get(realm.name()).remove(sReference.asString());

					break;

				case REMOVE:

					if (sectionApplies) {

						// Invoke form hook

						Section snapshot = getSectionSnapshot(realm.name(), sReference.asString());
						assert snapshot != null;

						getFormSectionMap().get(realm.name()).put(sReference.asString(), snapshot);

					}

					break;

				case UPDATE:

					if ((s.getTitle() != null || s.getSummary() != null) && sectionApplies) {

						Section section = getFormSectionMap().get(realm.name()).get(sReference.asString());

						Section snapshot = getSectionSnapshot(realm.name(), sReference.asString());
						section.importData(snapshot, false, false);

						getFormRefs().get(realm.name()).remove(sReference.asString());

						List<Class<? extends Realm>> refs = getFormSiblingsRefs().get(realm.name())
								.get(sReference.asString());
					}

					s.getFields().forEach(f -> {

						Reference fReference = f.getReference();
						String canonicalRef = sReference.asString() + "/" + fReference.asString();

						Boolean fieldApplies = getFormRefs().get(realm.name()).get(canonicalRef)
								.equals(realm.getClass());

						if (!fieldApplies) {
							return;
						}

						Iterator<AbstractField> j = section.getFields().iterator();

						while (j.hasNext()) {

							AbstractField field = j.next();

							if (!field.getReference().asString().equals(fReference.asString())) {
								continue;
							}

							AbstractField snapshot = getFieldSnapshot(realm.name(), sReference.asString(),
									fReference.asString());
							field.importData(snapshot, false);

							getFormRefs().get(realm.name()).remove(ca);

							break;
						}

					});

					break;

				}
			}

			for (RealmFunctionality f : realm.functionalities()) {

				Boolean functionalityApplies = getFunctionalitiesRefs().get(realm.name()).get(f.asString())
						.equals(realm.getClass());

				if (!functionalityApplies) {
					continue;
				}

				switch (f.getModifyType()) {
				case ADD:
					getFunctionalitiesMap().get(realm.name()).remove(f.asString());
					break;

				case REMOVE:
					getFunctionalitiesMap().get(realm.name()).add(f.getFunctionality());
					break;
				}
			}

		}

		getLoadedRealmClasses().remove(clazz);
	}

	/**
	 * This scans for role(s) that have the specified functionality added to them
	 * 
	 * @param functionality
	 * @return
	 */
	private List<String> getRoleReferencePoints(Functionality functionality) {

		List<String> roles = new ArrayList<>();

		getRoleFunctionalities().forEach((role, roleFunctionalities) -> {
			if (roleFunctionalities.contains(functionality.asString())) {
				roles.add(role);
			}
		});

		return roles;
	}

	@Override
	public void removeRoleFunctionalities(String role) {
		getRoleFunctionalities().remove(role);
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

	@Override
	public void addRoleRealm(String role, Realm realm) {
		List<String> roles = getRealmRoles().get(realm.name());

		if (roles == null) {
			roles = new ArrayList<>();
			getRealmRoles().put(realm.name(), roles);
		}

		roles.add(role);
	}

	@Override
	public void removeRoleRealm(String role) {

		String realmName = getRoleRealm(role);

		getRealmRoles().get(realmName).remove(role);
	}

	private List<String> getRoles(String realmName) {
		return getRealmRoles().get(realmName);
	}

	private String getRoleRealm(String role) {
		return getRealmRoles().entrySet().stream().filter(e -> e.getValue().contains(role)).map(e -> e.getKey())
				.findFirst().get();
	}

}
