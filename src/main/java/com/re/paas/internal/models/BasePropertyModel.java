package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.re.paas.api.annotations.BlockerBlockerTodo;
import com.re.paas.api.annotations.ThrowsAssertionError;
import com.re.paas.api.annotations.Todo;
import com.re.paas.api.annotations.hasAsync;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.TaskExecutionOutcome;
import com.re.paas.api.classes.ClientResources.HtmlCharacterEntities;
import com.re.paas.api.listable.ListingFilter;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.IndexedNameSpec;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.tasks.CronJob;
import com.re.paas.api.utils.Dates;
import com.re.paas.api.utils.Utils;
import com.re.paas.apps.rex.classes.spec.AdvancedPropertySearch;
import com.re.paas.apps.rex.classes.spec.CityFeaturesSpec;
import com.re.paas.apps.rex.classes.spec.ListedProperty;
import com.re.paas.apps.rex.classes.spec.ListedRentPropertySpec;
import com.re.paas.apps.rex.classes.spec.ListedSalePropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyAvailabilityStatus;
import com.re.paas.apps.rex.classes.spec.PropertyContractType;
import com.re.paas.apps.rex.classes.spec.PropertyFloorPlanSpec;
import com.re.paas.apps.rex.classes.spec.PropertyListingRequest;
import com.re.paas.apps.rex.classes.spec.PropertyListingStatus;
import com.re.paas.apps.rex.classes.spec.PropertyListingStatusSpec;
import com.re.paas.apps.rex.classes.spec.PropertyPOISpec;
import com.re.paas.apps.rex.classes.spec.PropertyPriceRuleSpec;
import com.re.paas.apps.rex.classes.spec.PropertySearchByAgentOrganization;
import com.re.paas.apps.rex.classes.spec.PropertySearchByCity;
import com.re.paas.apps.rex.classes.spec.PropertySpec;
import com.re.paas.apps.rex.classes.spec.PropertyType;
import com.re.paas.apps.rex.classes.spec.PropertyUpdateSpec;
import com.re.paas.internal.caching.CacheAdapter;
import com.re.paas.internal.caching.CacheType;
import com.re.paas.internal.classes.CronInterval;
import com.re.paas.internal.classes.spec.ClientSignature;
import com.re.paas.internal.classes.spec.ClientSignatureType;
import com.re.paas.internal.classes.spec.MultiplierType;
import com.re.paas.internal.classes.spec.PriceRuleOperator;
import com.re.paas.internal.classes.spec.YearlyPaymentPeriod;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.emailing.EmailMessageTemplate;
import com.re.paas.internal.emailing.EmailingModel;
import com.re.paas.internal.entites.BaseUserEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationEntity;
import com.re.paas.internal.entites.directory.CityFeaturesEntity;
import com.re.paas.internal.entites.directory.ListedPropertyEntity;
import com.re.paas.internal.entites.directory.ListedRentPropertyEntity;
import com.re.paas.internal.entites.directory.ListedSalePropertyEntity;
import com.re.paas.internal.entites.directory.PropertyDescriptionEntity;
import com.re.paas.internal.entites.directory.PropertyEntity;
import com.re.paas.internal.entites.directory.PropertyFeaturesEntity;
import com.re.paas.internal.entites.directory.PropertyFloorPlanEntity;
import com.re.paas.internal.entites.directory.PropertyListingStatusEntity;
import com.re.paas.internal.entites.directory.PropertyPOIEntity;
import com.re.paas.internal.entites.directory.PropertyPriceRuleEntity;
import com.re.paas.internal.entites.directory.UserSavedListEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.helpers.CacheHelper;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.helpers.SearchHelper;
import com.re.paas.internal.models.listables.IndexedNameTypes;
import com.re.paas.internal.utils.BackendObjectMarshaller;
import com.re.paas.internal.utils.RBUtils;

public class BasePropertyModel implements BaseModel {

	protected static final String CACHE_KEY_PROPERTY_PRICE_$PROPERTY = "CACHE_KEY_PROPERTY_PRICE_$PROPERTY";

	@Override
	public String path() {
		return "core/base_property";
	}

	@Override
	public void preInstall() {

		// Create default property attributes for all property types

		// Disable/Enable client side context creation for property listings
		ConfigModel.put(ConfigKeys.ENABLE_CLIENT_SIDE_PROPERTY_LISTING_CTX_CREATION, true);

		// Enable property price rules
		ConfigModel.put(ConfigKeys.IS_PROPERTY_PRICE_RULES_ENABLED, true);
	}

	@ModelMethod(functionality = RoleFunctionalities.SET_CITY_FEATURES)
	public static void setCityFeatutures(CityFeaturesSpec spec) {
		CityFeaturesEntity entity = EntityHelper.fromObjectModel(spec);
		ofy().save().entity(entity).now();
	}

	private static boolean isPriceRulesEnabled() {
		return BackendObjectMarshaller.unmarshalBool(ConfigModel.get(ConfigKeys.IS_PROPERTY_PRICE_RULES_ENABLED));
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_CITY_FEATURES)
	public static CityFeaturesSpec getCityFeatutures(Integer id) {
		CityFeaturesEntity entity = ofy().load().type(CityFeaturesEntity.class).id(id.toString()).safe();
		return EntityHelper.toObjectModel(entity);
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_TYPE_FEATURES)
	public static void newPropertyTypeFeature(PropertyType type, List<String> features) {

		List<Object> entities = new ArrayList<>();

		for (String feature : features) {
			PropertyFeaturesEntity e = new PropertyFeaturesEntity().setType(type.getValue())
					.setTitle(ClientRBRef.get(feature)).setDateCreated(Dates.now());

			entities.add(e);
		}

		ofy().save().entities(entities).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.DELETE_PROPERTY_TYPE_FEATURES)
	public static void deletePropertyTypeFeature(List<Long> ids) {
		ofy().delete().type(PropertyFeaturesEntity.class).ids(ids).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.LIST_PROPERTY_TYPE_FEATURES)
	public static Map<Long, ClientRBRef> listPropertyTypeFeatures(PropertyType type) {

		Map<Long, ClientRBRef> result = new HashMap<>();

		EntityUtils.query(PropertyFeaturesEntity.class, QueryFilter.get("type", type.getValue())).forEach(e -> {
			result.put(e.getId(), e.getTitle());
		});

		return result;
	}

	protected static PropertyEntity getProperty(Long id) {
		return ofy().load().type(PropertyEntity.class).id(id).safe();
	}

	private static Double getBasePrice(YearlyPaymentPeriod period, Double price) {
		return getBasePrice(period, LocaleModel.getUserNumberFormat().getCurrency().getCurrencyCode(), price);
	}

	private static Double getBasePrice(YearlyPaymentPeriod period, String currency, Double price) {

		Double pricePerYear = period.getMultiplierType() == MultiplierType.DIVIDE ? price / period.getRatio()
				: price * period.getRatio();

		Double basePrice = pricePerYear / CurrencyModel.getCurrencyRate(currency);

		return basePrice;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_CREATION_REQUEST)
	public static Long newPropertyCreationRequest(Long principal, PropertySpec spec) {

		BaseAgentModel.validateAgentOrganizationProvisioning(spec.getAgentOrganization(), principal);

		List<ListedPropertyEntity> listings = new ArrayList<>();

		spec.getListings().forEach(l -> {

			ListedPropertyEntity e = null;

			if (l instanceof ListedRentPropertySpec) {
				e = EntityHelper.fromObjectModel((ListedRentPropertySpec) l);
			} else if (l instanceof ListedSalePropertySpec) {
				e = EntityHelper.fromObjectModel((ListedSalePropertySpec) l);
			}

			listings.add(e);
		});

		PropertyEntity p = EntityHelper.fromObjectModel(spec)
				.setBasePrice(getBasePrice(spec.getPaymentPeriod(), spec.getCurrency(), spec.getPrice()))
				.setLastUpdatedBy(principal);

		ofy().save().entities(listings).now().keySet().forEach(k -> {
			p.addListing(k.getId());
		});

		ofy().save().entity(p).now();

		// update status to pending
		PropertyListingStatusSpec lhs = new PropertyListingStatusSpec().setPropertyId(p.getId()).setPrincipal(principal)
				.setListingStatus(PropertyListingStatus.PENDING_CREATION)
				.setMessage("property_creation_request_was_created").setDateCreated(Dates.now());

		updatePropertyStatus(lhs);

		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(p.getAgentOrganization())
				.safe();
		Long agentOrganizationAdmin = aoe.getAdmin();
		BaseUserEntity e = BaseUserModel.get(agentOrganizationAdmin);

		Map<String, Object> templateVariables = FluentHashMap.forValueMap()
				.with("recipientName", BaseUserModel.getPersonName(agentOrganizationAdmin, false))
				.with("propertyId", p.getId()).with("propertyName", p.getTitle())
				.with("dateCreated", Dates.toPrettyString(Dates.now()))
				.with("principal", BaseUserModel.getPersonName(principal, true));

		EmailMessageTemplate message = new EmailMessageTemplate(principal, LocaleModel.getUserLocale())
				.setPageTitle(RBModel.get(e.getPreferredLocale(), "property_creation_request_pending_email_title"))
				.setPreActionText(RBUtils.getParagraphs(e.getPreferredLocale(), "property_creation_request_pending_email_body",
						templateVariables));

		EmailingModel.sendMail(FluentArrayList.asList(e.getEmail()),
				ClientRBRef.get("property_creation_request_pending_email_title"), message);

		return p.getId();
	}

	/**
	 * Based on updates made, this function recalculates the base price of the
	 * property, as well as that of its price rules
	 */
	private static void recalculatePropertyPrices(PropertyEntity p, PropertyUpdateSpec spec) {

		if (!(spec.getPaymentPeriod() != null || spec.getCurrency() != null || spec.getPrice() != null)) {

			// No price-related updates were made. Save and return;
			ofy().save().entity(p).now();
			return;
		}

		// Refresh cached price data for this property
		CacheAdapter.del(CacheType.PERSISTENT,
				CACHE_KEY_PROPERTY_PRICE_$PROPERTY.replace("$PROPERTY", p.getId().toString()));

		List<Long> ruleIds = p.getPriceRules();
		Map<Long, PropertyPriceRuleEntity> ruleEntities = ofy().load().type(PropertyPriceRuleEntity.class).ids(ruleIds);

		if (spec.getPrice() != null) {

			// set price of price rules

			for (Entry<Long, PropertyPriceRuleEntity> e : ruleEntities.entrySet()) {

				PropertyPriceRuleEntity rule = e.getValue();
				Double price = getPrice(PriceRuleOperator.from(rule.getOperator()), rule.getPercentile(),
						spec.getPrice());

				rule.setPrice(price);
				e.setValue(rule);

				p.addPrice(e.getKey(), price);
			}
		}

		if (spec.getPaymentPeriod() != null || spec.getCurrency() != null || spec.getPrice() != null) {

			// set property base price
			p.setBasePrice(getBasePrice(spec.getPaymentPeriod(), spec.getCurrency(), spec.getPrice()));

			// set base price of price rules
			for (Entry<Long, PropertyPriceRuleEntity> e : ruleEntities.entrySet()) {

				PropertyPriceRuleEntity rule = e.getValue();
				Double basePrice = getPrice(PriceRuleOperator.from(rule.getOperator()), rule.getPercentile(),
						p.getBasePrice());

				rule.setBasePrice(basePrice);
				e.setValue(rule);

				p.addBasePrice(e.getKey(), basePrice);
			}
		}

		ofy().save().entities(p, ruleEntities.values());
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST)
	public static void newPropertyUpdateRequest(Long principal, PropertyUpdateSpec spec) {

		PropertyEntity p = getProperty(spec.getId());
		BaseAgentModel.validateAgentOrganizationProvisioning(p.getAgentOrganization(), principal);

		PropertyEntity u = PropertyEntity.getUpdate(p, spec).setLastUpdatedBy(principal);

		recalculatePropertyPrices(u, spec);

		// update status to pending
		PropertyListingStatusSpec lhs = new PropertyListingStatusSpec().setPropertyId(u.getId()).setPrincipal(principal)
				.setListingStatus(PropertyListingStatus.PENDING_UPDATE)
				.setMessage("property_update_request_was_created").setDateCreated(Dates.now());

		updatePropertyStatus(lhs);

		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(u.getAgentOrganization())
				.safe();
		Long agentOrganizationAdmin = aoe.getAdmin();
		BaseUserEntity e = BaseUserModel.get(agentOrganizationAdmin);

		Map<String, Object> templateVariables = FluentHashMap.forValueMap()
				.with("recipientName", BaseUserModel.getPersonName(agentOrganizationAdmin, false))
				.with("propertyId", u.getId()).with("propertyName", u.getTitle())
				.with("dateCreated", Dates.toPrettyString(Dates.now()))
				.with("principal", BaseUserModel.getPersonName(principal, true));

		EmailMessageTemplate message = new EmailMessageTemplate(principal, LocaleModel.getUserLocale())
				.setPageTitle(RBModel.get(e.getPreferredLocale(), "property_update_request_pending_email_title"))
				.setPreActionText(RBUtils.getParagraphs(e.getPreferredLocale(), "property_update_request_pending_email_body",
						templateVariables));

		EmailingModel.sendMail(FluentArrayList.asList(e.getEmail()),
				ClientRBRef.get("property_update_request_pending_email_title"), message);

	}

	@ModelMethod(functionality = RoleFunctionalities.REVIEW_PROPERTY_REQUEST)
	public static void acceptPropertyRequest(Long principal, Long id) {

		PropertyEntity p = getProperty(id);

		PropertyListingStatus currentStatus = PropertyListingStatus.from(p.getListingStatus());

		String statusMessage = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_was_accepted"
				: "property_update_request_was_accepted";

		// update status to live
		PropertyListingStatusSpec lhs = new PropertyListingStatusSpec().setPropertyId(id).setPrincipal(principal)
				.setListingStatus(PropertyListingStatus.LIVE).setMessage(statusMessage).setDateCreated(Dates.now());

		updatePropertyStatus(lhs, true);

		// email admin of agentOrganization

		String emailTitle = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_acceptance_email_title"
				: "property_update_request_acceptance_email_title";

		String emailBody = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_acceptance_email_body"
				: "property_update_request_acceptance_email_body";

		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(p.getAgentOrganization())
				.safe();
		Long agentOrganizationAdmin = aoe.getAdmin();
		BaseUserEntity e = BaseUserModel.get(agentOrganizationAdmin);

		Map<String, Object> templateVariables = FluentHashMap.forValueMap()
				.with("recipientName", BaseUserModel.getPersonName(agentOrganizationAdmin, false))
				.with("propertyId", p.getId()).with("propertyName", p.getTitle())
				.with("dateCreated", Dates.toPrettyString(Dates.now()))
				.with("principal", BaseUserModel.getPersonName(p.getLastUpdatedBy(), true));

		EmailMessageTemplate message = new EmailMessageTemplate(principal, LocaleModel.getUserLocale())
				.setPageTitle(emailTitle)
				.setPreActionText(RBUtils.getParagraphs(e.getPreferredLocale(), emailBody,
						templateVariables));
		
		EmailingModel.sendMail(FluentArrayList.asList(e.getEmail()), ClientRBRef.get(emailTitle), message);

		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.REVIEW_PROPERTY_REQUEST)
	public static void declinePropertyRequest(Long principal, Long id) {

		PropertyEntity p = getProperty(id);

		PropertyListingStatus currentStatus = PropertyListingStatus.from(p.getListingStatus());

		String statusMessage = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_was_declined"
				: "property_update_request_was_declined";

		// update status to live
		PropertyListingStatusSpec lhs = new PropertyListingStatusSpec().setPropertyId(id).setPrincipal(principal)
				.setListingStatus(PropertyListingStatus.NEEDS_ATTENTION).setMessage(statusMessage)
				.setDateCreated(Dates.now());

		updatePropertyStatus(lhs, true);

		// email admin of agentOrganization

		String emailTitle = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_decline_email_title"
				: "property_update_request_decline_email_title";

		String emailBody = currentStatus == PropertyListingStatus.PENDING_CREATION
				? "property_creation_request_decline_email_body"
				: "property_update_request_decline_email_body";

		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(p.getAgentOrganization())
				.safe();
		Long agentOrganizationAdmin = aoe.getAdmin();
		BaseUserEntity e = BaseUserModel.get(agentOrganizationAdmin);

		Map<String, Object> templateVariables = FluentHashMap.forValueMap()
				.with("recipientName", BaseUserModel.getPersonName(agentOrganizationAdmin, false))
				.with("propertyId", p.getId()).with("propertyName", p.getTitle())
				.with("dateCreated", Dates.toPrettyString(Dates.now()))
				.with("principal", BaseUserModel.getPersonName(p.getLastUpdatedBy(), true));

		EmailMessageTemplate message = new EmailMessageTemplate(principal, LocaleModel.getUserLocale())
				.setPageTitle(RBModel.get(e.getPreferredLocale(), emailTitle))
				.setPreActionText(RBUtils.getParagraphs(e.getPreferredLocale(), emailBody,
						templateVariables));
		
		EmailingModel.sendMail(FluentArrayList.asList(e.getEmail()), ClientRBRef.get(emailTitle), message);

		// Add to activity stream
	}

	private static PropertyEntity updatePropertyStatus(PropertyListingStatusSpec spec) {
		return updatePropertyStatus(spec, true);
	}

	/**
	 * This is a helper method that should normally be called when property statuses
	 * need to be updated. It also performs system-critical operations, such as
	 * modifying the search index, and the corresponding cache list
	 */
	@hasAsync
	protected static PropertyEntity updatePropertyStatus(PropertyListingStatusSpec spec, boolean savePropertyEntity) {

		PropertyListingStatusEntity e = EntityHelper.fromObjectModel(spec);

		ofy().save().entity(e).now();

		PropertyEntity p = getProperty(spec.getPropertyId());

		p.setListingStatus(spec.getListingStatus().getValue()).addListingStatusHistory(e.getId());

		if (savePropertyEntity) {
			ofy().save().entity(p);
		}

		ListingFilter cityFilter = new ListingFilter("city", p.getCity());
		ListingFilter agentOrganizationFilter = new ListingFilter("agentOrganization", p.getAgentOrganization());

		switch (spec.getListingStatus()) {

		case LIVE:

			// Add to cache list
			SearchModel.addCachedListKey(IndexedNameTypes.PROPERTY_SPEC, FluentArrayList.asList(cityFilter), p.getId());
			SearchModel.addCachedListKey(IndexedNameTypes.PROPERTY_SPEC, FluentArrayList.asList(agentOrganizationFilter),
					p.getId());

			// Add to search index

			String address = p.getAddress();
			String cityName = LocationModel.getCityName(p.getCity().toString());

			Matcher addressMatcher = Pattern.compile("\\A[0-9]+[ ]*(\\Q,\\E)*[ ]*").matcher(p.getAddress());
			while (addressMatcher.find()) {
				address = address.replaceFirst(Pattern.quote(addressMatcher.group()), "");
			}

			SearchModel.addIndexedName(
					IndexedNameSpec.get(p.getId().toString(), address, p.getZipCode().toString(), cityName),
					IndexedNameTypes.PROPERTY_SPEC);

			break;

		default:

			// Remove from cache list
			SearchModel.removeCachedListKey(IndexedNameTypes.PROPERTY_SPEC, FluentArrayList.asList(cityFilter), p.getId());
			SearchModel.removeCachedListKey(IndexedNameTypes.PROPERTY_SPEC, FluentArrayList.asList(agentOrganizationFilter),
					p.getId());

			// Remove from search index
			SearchModel.removeIndexedName(p.getId().toString(), IndexedNameTypes.PROPERTY_SPEC);

			break;
		}

		// Add to activity stream
		return p;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_DELETION_REQUEST)
	public static void newPropertyDeletionRequest(Long principal, Long property) {

		PropertyEntity p = ofy().load().type(PropertyEntity.class).id(property).safe();
		Long agentOrganization = p.getAgentOrganization();

		BaseAgentModel.validateAgentOrganizationProvisioning(agentOrganization, principal);

		// schedule deletion

		String taskName = ClientRBRef.get("deletion_task").toString() + HtmlCharacterEntities.SPACE
				+ ClientRBRef.get("for").toString() + HtmlCharacterEntities.SPACE + p.getTitle();
		CronInterval interval = CronInterval.EVERY_DAY;

		CronJob task = CronJob.get(() -> {
			com.re.paas.internal.models.BasePropertyModel.deleteProperty(principal, p.getId());
			return TaskExecutionOutcome.SUCCESS.setMessage(ClientRBRef.get("property_deletion_task_was_completed"));
		});

		TaskModel.newTask(taskName, interval, task, 1, false);

		// send mail to admin of agentOrganization of principal

		LocalDate executionTime = TaskModel.getNextExecutionTime(interval);
		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(agentOrganization).safe();
		Long agentOrganizationAdmin = aoe.getAdmin();
		BaseUserEntity e = BaseUserModel.get(agentOrganizationAdmin);

		Map<String, Object> templateVariables = FluentHashMap.forValueMap()
				.with("recipientName", BaseUserModel.getPersonName(agentOrganizationAdmin, false))
				.with("propertyId", p.getId()).with("propertyName", p.getTitle())
				.with("dateScheduled", Dates.toPrettyString(Utils.toDate(executionTime)))
				.with("dateCreated", Dates.toPrettyString(Dates.now()))
				.with("principal", BaseUserModel.getPersonName(principal, true));
		
		EmailMessageTemplate message = new EmailMessageTemplate(principal, LocaleModel.getUserLocale())
				.setPageTitle(RBModel.get(e.getPreferredLocale(), "property_deletion_request_email_title"))
				.setPreActionText(RBUtils.getParagraphs(e.getPreferredLocale(), "property_deletion_request_email_body",
						templateVariables));

		EmailingModel.sendMail(FluentArrayList.asList(e.getEmail()),
				ClientRBRef.get("property_deletion_request_email_title"), message);

		// update status to pending
		PropertyListingStatusSpec lhs = new PropertyListingStatusSpec().setPropertyId(p.getId()).setPrincipal(principal)
				.setListingStatus(PropertyListingStatus.PENDING_DELETION)
				.setMessage("property_deletion_request_was_created").setDateCreated(Dates.now());

		updatePropertyStatus(lhs);

		// Add to activity stream
	}

	/**
	 * This method is called by the cron model, when the grace period given for
	 * property deletion has elapsed
	 */
	@BlockerBlockerTodo
	protected static void deleteProperty(Long principal, Long id) {

		PropertyEntity e = getProperty(id);
		BaseAgentModel.validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		/// consolidate, delete e, listings, POI, e.t.c

		SearchModel.removeIndexedName(e.getId().toString(), IndexedNameTypes.PROPERTY_SPEC);

		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST)
	public static void setPropertyDescription(Long principal, Long id, String description) {

		PropertyEntity e = getProperty(id);
		BaseAgentModel.validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		PropertyDescriptionEntity entity = new PropertyDescriptionEntity().setId(id).setDescription(description)
				.setDateCreated(Dates.now());

		ofy().save().entity(entity).now();

		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.VIEW_PROPERTY)
	public static String getPropertyDescription(Long id) {
		PropertyDescriptionEntity e = ofy().load().type(PropertyDescriptionEntity.class).id(id).now();

		// Add to activity stream

		return e != null ? e.getDescription() : null;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST)
	public static void setPropertyPOI(Long principal, PropertyPOISpec spec) {

		PropertyEntity e = getProperty(spec.getId());
		BaseAgentModel.validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		PropertyPOIEntity entity = EntityHelper.fromObjectModel(spec).setDateCreated(Dates.now());

		// Add to activity stream

		ofy().save().entity(entity).now();
	}

	@ModelMethod(functionality = RoleFunctionalities.VIEW_PROPERTY)
	public static PropertyPOISpec getPropertyPOI(Long id) {
		PropertyPOIEntity e = ofy().load().type(PropertyPOIEntity.class).id(id).now();

		// Add to activity stream

		return e != null ? EntityHelper.toObjectModel(e) : null;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST)
	public static Long addPropertyFloorPlan(Long principal, Long propertyId, PropertyFloorPlanSpec spec) {

		PropertyEntity e = getProperty(propertyId);
		BaseAgentModel.validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		PropertyFloorPlanEntity entity = EntityHelper.fromObjectModel(spec);

		Long floorPlan = ofy().save().entity(entity).now().getId();

		// Add to activity stream

		return ofy().save().entity(e.addFloorPlan(floorPlan)).now().getId();
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST)
	public static void removePropertyFloorPlan(Long principal, Long propertyId, Long id) {

		PropertyEntity e = getProperty(propertyId);
		BaseAgentModel.validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		ofy().delete().type(PropertyFloorPlanEntity.class).id(id);

		e.removeFloorPlan(id);

		ofy().save().entity(e).now();

		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.VIEW_PROPERTY)
	public static Map<Long, PropertyFloorPlanSpec> getPropertyFloorPlan(List<Long> ids) {

		Map<Long, PropertyFloorPlanSpec> result = new HashMap<Long, PropertyFloorPlanSpec>();

		ofy().load().type(PropertyFloorPlanEntity.class).ids(ids).forEach((k, v) -> {
			result.put(k, EntityHelper.toObjectModel(v));
		});

		// Add to activity stream

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.ADD_TO_USER_SAVED_LIST)
	public static void addToUserSavedList(Long principal, Long userId, Long propertyId) {
		addToSavedList(userId, propertyId);
		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.ADD_TO_OWN_SAVED_LIST)
	public static void addToOwnSavedList(Long principal, Long propertyId) {
		addToSavedList(principal, propertyId);
		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.REMOVE_FROM_USER_SAVED_LIST)
	public static void removeFromUserSavedList(Long principal, Long userId, Long propertyId) {
		removeFromSavedList(userId, propertyId);
		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.REMOVE_FROM_OWN_SAVED_LIST)
	public static void removeFromOwnSavedList(Long principal, Long propertyId) {
		removeFromSavedList(principal, propertyId);
		// Add to activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_USER_SAVED_LIST)
	public static List<Long> getUserSavedList(Long principal, Long userId) {

		List<Long> properties = getSavedList(userId);

		// Add to activity stream

		return properties;
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_OWN_SAVED_LIST)
	public static List<Long> getOwnSavedList(Long principal) {

		List<Long> properties = getSavedList(principal);

		// Add to activity stream

		return properties;
	}

	private static void addToSavedList(Long userId, Long propertyId) {

		UserSavedListEntity e = ofy().load().type(UserSavedListEntity.class).id(userId).now();
		if (e == null) {
			e = new UserSavedListEntity().setId(userId);
		}

		e.addProperty(propertyId).setDateUpdated(Dates.now());
		ofy().save().entity(e).now();
	}

	private static void removeFromSavedList(Long userId, Long propertyId) {

		UserSavedListEntity e = ofy().load().type(UserSavedListEntity.class).id(userId).safe();
		e.removeProperty(propertyId).setDateUpdated(Dates.now());
		ofy().save().entity(e).now();
	}

	private static List<Long> getSavedList(Long userId) {
		UserSavedListEntity e = ofy().load().type(UserSavedListEntity.class).id(userId).now();
		return e != null ? e.getProperties() : new ArrayList<>();
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_LISTING)
	public static Long newPropertyListing(Long principal, ListedProperty spec) {
		if (spec instanceof ListedRentPropertySpec) {
			return newPropertyListing(principal, (ListedRentPropertySpec) spec);
		} else {
			return newPropertyListing(principal, (ListedSalePropertySpec) spec);
		}
	}

	private static Long newPropertyListing(Long principal, ListedRentPropertySpec spec) {

		BaseAgentModel.validateAgentOrganizationProvisioning(spec.getAgentOrganizationId(), principal);

		ListedRentPropertyEntity e = EntityHelper.fromObjectModel(spec);
		Long id = ofy().save().entity(e).now().getId();

		// Add to Activity stream

		return id;
	}

	private static Long newPropertyListing(Long principal, ListedSalePropertySpec spec) {

		BaseAgentModel.validateAgentOrganizationProvisioning(spec.getAgentOrganizationId(), principal);

		ListedSalePropertyEntity e = EntityHelper.fromObjectModel(spec);
		Long id = ofy().save().entity(e).now().getId();

		// Add to Activity stream

		return id;
	}
	
	@ModelMethod(functionality = RoleFunctionalities.UPDATE_PROPERTY_LISTING)
	public static void updatePropertyListing(Long principal, ListedProperty spec) {
		if (spec instanceof ListedRentPropertySpec) {
			 updatePropertyListing(principal, (ListedRentPropertySpec) spec);
		} else {
			updatePropertyListing(principal, (ListedSalePropertySpec) spec);
		}
	}

	private static void updatePropertyListing(Long principal, ListedRentPropertySpec spec) {

		BaseAgentModel.validateAgentOrganizationProvisioning(spec.getAgentOrganizationId(), principal);

		ListedRentPropertyEntity e = EntityHelper.fromObjectModel(spec).setId(spec.getId());
		ofy().save().entity(e).now();

		// Add to Activity stream

	}

	private static void updatePropertyListing(Long principal, ListedSalePropertySpec spec) {

		BaseAgentModel.validateAgentOrganizationProvisioning(spec.getAgentOrganizationId(), principal);

		ListedSalePropertyEntity e = EntityHelper.fromObjectModel(spec).setId(spec.getId());
		ofy().save().entity(e).now();

		// Add to Activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.DELETE_PROPERTY_LISTING)
	public static void deletePropertyListing(Long principal, Long id) {

		ListedPropertyEntity lp = ofy().load().type(ListedPropertyEntity.class).id(id).safe();

		BaseAgentModel.validateAgentOrganizationProvisioning(lp.agentOrganizationId, principal);

		ofy().delete().type(ListedPropertyEntity.class).id(lp.id).now();

		// Add to Activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.GET_PROPERTY_LISTINGS)
	public static List<ListedProperty> getPropertyListings(Long principal, Long propertyId) {

		PropertyEntity p = getProperty(propertyId);

		BaseAgentModel.validateAgentOrganizationProvisioning(p.getAgentOrganization(), principal);

		List<ListedProperty> result = new ArrayList<ListedProperty>();

		ofy().load().type(ListedPropertyEntity.class).ids(p.getListings()).forEach((k, v) -> {
			if (v instanceof ListedRentPropertyEntity) {
				result.add(EntityHelper.toObjectModel((ListedRentPropertyEntity) v));
			} else if (v instanceof ListedSalePropertyEntity) {
				result.add(EntityHelper.toObjectModel((ListedSalePropertyEntity) v));
			}
		});

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS)
	public static void updatePropertyListingAvailabilityStatus(Long principal, Long id,
			PropertyAvailabilityStatus status) {

		ListedPropertyEntity p = ofy().load().type(ListedPropertyEntity.class).id(id).safe();

		BaseAgentModel.validateAgentOrganizationProvisioning(p.agentOrganizationId, principal);

		p.availabilityStatus = status.getValue();

		ofy().save().entity(p).now();

		// Add to Activity stream
	}

	@ModelMethod(functionality = RoleFunctionalities.LIST_PROPERTY)
	public static String listProperties(Long principal, ClientSignature clientSignature, PropertyListingRequest req,
			Integer pageSize) {

		List<Long> keys = new ArrayList<>();

		if (req instanceof PropertySearchByCity) {
			keys = listPropertyByCity((PropertySearchByCity) req);
		}

		if (req instanceof PropertySearchByAgentOrganization) {
			keys = listPropertyByAgentOrganization((PropertySearchByAgentOrganization) req);
		}

		if (req instanceof AdvancedPropertySearch) {
			keys = advancedPropertySearch(clientSignature, (AdvancedPropertySearch) req);
		}

		Map<Long, Long> finalKeyMap = reduceByContractType(keys, req.getContractType());

		List<String> finalKeyList = new ArrayList<>(finalKeyMap.size());
		finalKeyMap.forEach((k, v) -> {
			finalKeyList.add(k + "_" + v);
		});

		// Create context
		return SearchModel.newContext(IndexedNameTypes.PROPERTY_SPEC, finalKeyList, pageSize);
	}

	private static List<Long> listPropertyByCity(PropertySearchByCity req) {

		return SearchHelper.getKeys(IndexedNameTypes.PROPERTY_SPEC, new ListingFilter("city", req.getCityId()), () -> {
			return EntityUtils.lazyQuery(PropertyEntity.class, QueryFilter.get("city", req.getCityId())).keys();
		});
	}

	private static List<Long> listPropertyByAgentOrganization(PropertySearchByAgentOrganization req) {

		return SearchHelper.getKeys(IndexedNameTypes.PROPERTY_SPEC,
				new ListingFilter("agentOrganization", req.getAgentOrganization()), () -> {
					return EntityUtils.lazyQuery(PropertyEntity.class,
							QueryFilter.get("agentOrganization", req.getAgentOrganization().toString())).keys();
				});
	}

	@ThrowsAssertionError
	@Todo("Always use forEach to make computations run in parallel")
	private static List<Long> advancedPropertySearch(ClientSignature clientSignature, AdvancedPropertySearch req) {

		assert req.getMinPrice() == null || req.getMaxPrice() == null || req.getPaymentPeriod() != null;

		// Procedure 1: Query for <phraseKeys> and <filterKeys>

		List<Long> phraseKeys = null;

		if (req.getSearchTerm() != null) {
			assert req.getSearchTerm().length() > 3;
			phraseKeys = SearchHelper.getKeys(IndexedNameTypes.PROPERTY_SPEC, req.getSearchTerm());
		}

		List<QueryFilter> filterList = new ArrayList<QueryFilter>();

		if (req.getPropertyType() != null) {
			filterList.add(QueryFilter.get("type", req.getPropertyType().getValue()));
		}

		if (req.getRoomCount() != null) {
			filterList.add(QueryFilter.get("roomCount", req.getRoomCount()));
		}

		if (req.getBathroomCount() != null) {
			filterList.add(QueryFilter.get("bathroomCount", req.getBathroomCount()));
		}

		if (req.getMinArea() != null) {
			filterList.add(QueryFilter.get("area >=", req.getMinArea()));
		}

		if (req.getMaxArea() != null) {
			filterList.add(QueryFilter.get("area <=", req.getMaxArea()));
		}

		if (!isPriceRulesEnabled()) {

			if (req.getMinPrice() != null) {
				filterList
						.add(QueryFilter.get("basePrice >=", getBasePrice(req.getPaymentPeriod(), req.getMinPrice())));
			}

			if (req.getMaxPrice() != null) {
				filterList
						.add(QueryFilter.get("basePrice <=", getBasePrice(req.getPaymentPeriod(), req.getMaxPrice())));
			}
		}

		if (req.isForcePaymentPeriod() && req.getPaymentPeriod() != null) {
			filterList.add(QueryFilter.get("paymentPeriod", req.getPaymentPeriod().getValue()));
		}

		QueryFilter[] filters = filterList.toArray(new QueryFilter[filterList.size()]);

		List<Long> filterKeys = SearchHelper.getKeys(IndexedNameTypes.PROPERTY_SPEC, ListingFilter.fromQueryFilter(filters),
				() -> {
					return EntityUtils.lazyQuery(PropertyEntity.class, filters).keys();
				});

		// Procedure 2: Merge <phraseKeys> and <filterKeys>

		final List<Long> keys = new ArrayList<>();

		if (phraseKeys == null) {
			keys.addAll(filterKeys);
		} else {

			phraseKeys.forEach(l -> {
				if (filterKeys.contains(l)) {
					keys.add(l);
				}
			});

		}

		if (!isPriceRulesEnabled() || (req.getMinPrice() == null && req.getMaxPrice() == null)) {
			return keys;
		}

		// Procedure 3: Consolidate with price

		List<Long> result = new ArrayList<>();

		keys.forEach(k -> {

			Double computedPrice = computePrice(clientSignature, k);

			if (req.getMinPrice() != null && computedPrice < getBasePrice(req.getPaymentPeriod(), req.getMinPrice())) {
				return;
			}

			if (req.getMaxPrice() != null && computedPrice > getBasePrice(req.getPaymentPeriod(), req.getMaxPrice())) {
				return;
			}

			result.add(k);
		});

		return result;
	}

	private static Double computePrice(ClientSignature clientSignature, Long propertyId) {

		// Build cache key

		final StringBuilder cacheKey = new StringBuilder();

		cacheKey.append("COMPUTED_PRICE").append("__property_" + propertyId);

		clientSignature.getValues().forEach((k, v) -> {
			cacheKey.append("__" + k.name() + "_" + v);
		});

		Double result = (Double) CacheAdapter.get(CacheType.PERSISTENT, cacheKey.toString());

		if (result != null) {
			return result;
		}

		PropertyEntity p = getProperty(propertyId);

		// Get price rules for this property

		Map<Long, PropertyPriceRuleEntity> priceRules = ofy().load().type(PropertyPriceRuleEntity.class)
				.ids(p.getPriceRules());

		Map<Long, Integer> ruleScores = new HashMap<>();

		Map<ClientSignatureType, String> customerRules = clientSignature.getValues();

		priceRules: for (PropertyPriceRuleEntity e : priceRules.values()) {

			// Evaluate rules

			int currentScore = 0;

			Map<ClientSignatureType, String> rules = e.getRules();

			for (Entry<ClientSignatureType, String> rule : rules.entrySet()) {

				String value = customerRules.get(rule.getKey());
				if (value == null || !value.equals(rule.getValue())) {
					// Fall through to next price rules
					continue priceRules;
				}

				currentScore += rule.getKey().getScore();
			}

			ruleScores.put(e.getId(), currentScore);
		}

		if (!ruleScores.isEmpty()) {

			// Select highest score

			Entry<Long, Integer> highestScore = null;

			for (Entry<Long, Integer> score : ruleScores.entrySet()) {
				if (highestScore == null) {
					highestScore = score;
				} else if (highestScore.getValue() < score.getValue()) {
					highestScore = score;
				}
			}

			return priceRules.get(highestScore.getKey()).getBasePrice();
		}

		CacheAdapter.put(CacheType.PERSISTENT, cacheKey.toString(), p.getBasePrice());

		CacheHelper.addToListOrCreate(CacheType.PERSISTENT,
				CACHE_KEY_PROPERTY_PRICE_$PROPERTY.replace("$PROPERTY", p.getId().toString()), cacheKey.toString());

		return p.getBasePrice();
	}

	private static Map<Long, Long> reduceByContractType(List<Long> keys, PropertyContractType contractType) {

		final Map<Long, Long> result = new HashMap<>();

		keys.forEach(k -> {

			PropertyEntity p = getProperty(k);

			for (Long listing : p.getListings()) {

				ListedPropertyEntity l = ofy().load().type(ListedPropertyEntity.class).id(listing).now();

				if (l.availabilityStatus == PropertyAvailabilityStatus.NOT_AVAILABLE.getValue()) {
					continue;
				}

				if (contractType == null) {
					result.put(p.getId(), l.id);
					// break;
				}

				if (contractType == PropertyContractType.RENT && l instanceof ListedRentPropertyEntity) {
					result.put(p.getId(), l.id);
					break;
				} else if (contractType == PropertyContractType.SALE && l instanceof ListedSalePropertyEntity) {
					result.put(p.getId(), l.id);
					break;
				}
			}

		});

		return result;
	}

	private static Double getPrice(PriceRuleOperator operator, Double percentile, Double price) {

		Double percentileValue = (percentile / 100) * price;

		Double result = null;

		switch (operator) {
		case MINUS:
			result = price - percentileValue;
			break;
		case PLUS:
			result = price + percentileValue;
			break;
		}

		return result;
	}

	@ModelMethod(functionality = RoleFunctionalities.CREATE_PROPERTY_PRICE_RULE)
	public static Long newPropertyPriceRule(Long principal, PropertyPriceRuleSpec spec) {

		PropertyEntity p = getProperty(spec.getPropertyId());
		BaseAgentModel.validateAgentOrganizationProvisioning(p.getAgentOrganization(), principal);

		Double price = getPrice(spec.getOperator(), spec.getPercentile(), p.getPrice());
		Double basePrice = getPrice(spec.getOperator(), spec.getPercentile(), p.getBasePrice());

		PropertyPriceRuleEntity e = EntityHelper.fromObjectModel(spec).setPrice(price).setBasePrice(basePrice);

		ofy().save().entity(e).now();

		p.addPriceRule(e.getId());

		p.addPrice(e.getId(), price);
		p.addBasePrice(e.getId(), basePrice);

		ofy().save().entity(p);

		return e.getId();
	}

	@ModelMethod(functionality = RoleFunctionalities.UPDATE_PROPERTY_PRICE_RULE)
	public static void updatePropertyPriceRule(Long principal, Long id, PropertyPriceRuleSpec spec) {

		PropertyPriceRuleEntity e = ofy().load().type(PropertyPriceRuleEntity.class).id(id).now();

		assert e.getPropertyId().equals(spec.getPropertyId());

		PropertyEntity p = getProperty(e.getPropertyId());

		BaseAgentModel.validateAgentOrganizationProvisioning(p.getAgentOrganization(), principal);

		Double price = getPrice(spec.getOperator(), spec.getPercentile(), p.getPrice());
		Double basePrice = getPrice(spec.getOperator(), spec.getPercentile(), p.getBasePrice());

		e.setRules(spec.getRules()).setOperator(spec.getOperator().getValue()).setPercentile(spec.getPercentile())
				.setPrice(price).setBasePrice(basePrice).setDateUpdated(Dates.now());

		p.addPrice(e.getId(), price);
		p.addBasePrice(e.getId(), basePrice);

		ofy().save().entities(e, p);
	}

	@ModelMethod(functionality = RoleFunctionalities.DELETE_PROPERTY_PRICE_RULE)
	public static void deletePropertyPriceRule(Long principal, Long id) {

		PropertyPriceRuleEntity e = ofy().load().type(PropertyPriceRuleEntity.class).id(id).now();

		PropertyEntity p = getProperty(e.getPropertyId());

		BaseAgentModel.validateAgentOrganizationProvisioning(p.getAgentOrganization(), principal);

		p.removePriceRule(e.getId());
		p.removePrice(e.getId());
		p.removeBasePrice(e.getId());

		ofy().save().entity(p);

		ofy().delete().type(PropertyPriceRuleEntity.class).id(e.getId());
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
