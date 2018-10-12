package com.re.paas.internal.models;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.googlecode.objectify.cmd.QueryKeys;
import com.re.paas.api.annotations.PlatformInternal;
import com.re.paas.api.classes.FluentHashMap;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.ModelMethod;
import com.re.paas.api.models.classes.IndexedNameSpec;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.utils.Dates;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationReviewSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationSpec;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationWhistleblowMessageSpec;
import com.re.paas.apps.rex.classes.spec.AgentSpec;
import com.re.paas.apps.rex.classes.spec.IssueResolution;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.realms.AgentRealm;
import com.re.paas.apps.rex.realms.OrganizationAdminRealm;
import com.re.paas.internal.billing.BaseCardInfo;
import com.re.paas.internal.billing.BillingAddress;
import com.re.paas.internal.billing.InvoiceSpec;
import com.re.paas.internal.billing.PaymentRequest;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.entites.directory.AgentAvailabilityScheduleEntity;
import com.re.paas.internal.entites.directory.AgentEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationAdminEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationAvailabilityScheduleEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationMessageEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationReviewEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationWhistleblowMessageEntity;
import com.re.paas.internal.fusion.functionalities.RoleFunctionalities;
import com.re.paas.internal.models.helpers.EntityHelper;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.listables.IndexedNameTypes;
import com.re.paas.internal.realms.AdminRealm;
import com.re.paas.internal.users.RoleRealm;

public class BaseAgentModel implements BaseModel {

	protected static final String CACHE_KEY_AGENT_AVAILABILITY_SCHEDULE_$AGENT = "CACHE_KEY_AGENT_AVAILABILITY_SCHEDULE_$AGENT";

	@Override
	public String path() {
		return "core/base_agent";
	}

	@Override
	public void preInstall() {

	}

	public static void newAgentOrganizationAdmin(Long userId, Long agentOrganization) {
		AgentOrganizationAdminEntity e = new AgentOrganizationAdminEntity().setId(userId)
				.setAgentOrganization(agentOrganization);
		ofy().save().entity(e).now();

		// Add to activity stream
	}

	public static Long newAgentOrganization(AgentOrganizationSpec spec) {

		Integer defaultRating = Integer.parseInt(ConfigModel.get(ConfigKeys.DEFAULT_AGENT_ORGANIZATION_RATING));

		AgentOrganizationEntity e = EntityHelper.fromObjectModel(spec).setRating(defaultRating);
		Long id = ofy().save().entity(e).now().getId();

		// Add to activity stream

		// Add to search index
		SearchModel.addIndexedName(new IndexedNameSpec(e.getId().toString(), e.getName()),
				IndexedNameTypes.AGENT_ORGANIZATION);

		// Create new billing context
		String currency = LocationModel.getCurrencyCode(spec.getCountry());
		BillingModel.newInvoice(InvoiceSpec.create(id, currency, null));

		createAgentOrganizationAvailabilitySchedule(id);

		// Add to activity stream

		return id;
	}

	protected static PaymentRequest createPaymentRequest(Long agentOrganization) {

		PaymentRequest req = new PaymentRequest();

		AgentOrganizationEntity e = ofy().load().type(AgentOrganizationEntity.class).id(agentOrganization).safe();

		req.setCustomerEmail(e.getEmail()).setCustomerId(e.getId()).setCustomerPhone(e.getPhone().toString());

		BillingAddress billingAddress = new BillingAddress();

		String[] address = e.getAddress().split(",[\\s]*");

		billingAddress.setHouseNumberOrName(address[0]);
		billingAddress.setStreet(e.getAddress().replace(address[0], ""));
		billingAddress.setPostalCode(e.getPostalCode().toString());
		billingAddress.setCity(LocationModel.getCityName(e.getCity().toString()));
		billingAddress.setStateOrProvince(e.getTerritory());
		billingAddress.setCountry(e.getCountry());

		req.setBillingAddress(billingAddress);

		BaseCardInfo cardInfo = BillingModel.getPaymentMethod(agentOrganization);
		req.setCardInfo(cardInfo);

		return req;
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.LIST_AGENT_ORGANIZATION_NAMES)
	public static Map<Long, String> listAgentOrganizationNames(String territory) {
		Map<Long, String> result = new HashMap<Long, String>();
		EntityUtils.query(AgentOrganizationEntity.class, QueryFilter.get("territory", territory)).forEach(e -> {
			result.put(e.getId(), e.getName());
		});
		return result;
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION, isFeatureReady = false)
	public static void updateAgentOrganization(Long principal, AgentOrganizationSpec spec) {

		validateAgentOrganizationProvisioning(spec.getId(), principal);
		
		//@Todo
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION, isFeatureReady = false)
	public static void deleteAgentOrganization(Long id) {
		
		// ofy().delete().type(AgentOrganizationEntity.class).id(id).now();

		// Delete all properties, admin/agents accounts, e.t.c

		//@Todo
		
		// Remove from search index
		SearchModel.removeIndexedName(id.toString(), IndexedNameTypes.AGENT_ORGANIZATION);
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION)
	public static AgentOrganizationSpec getAgentOrganization(Long id) {

		AgentOrganizationEntity e = ofy().load().type(AgentOrganizationEntity.class).id(id).safe();
		return EntityHelper.toObjectModel(e);
	}

	@PlatformInternal
	public static String getAgentOrganizationName(Long id) {
		AgentOrganizationEntity e = ofy().load().type(AgentOrganizationEntity.class).id(id).safe();
		return e.getName();
	}

	public static Long newAgent(Long userId, AgentSpec spec) {

		AgentEntity e = EntityHelper.fromObjectModel(spec).setId(userId);

		Long id = ofy().save().entity(e).now().getId();

		createAgentAvailabilitySchedule(id);

		// Add to activity stream

		return id;
	}

	@ModelMethod(functionality = AgentFunctionalities.Constants.DELETE_AGENT, isFeatureReady = false)
	public static void deleteAgent(Long id, Long principal) {
		
		validateAgentProvisioning(id, principal);
		
		ofy().delete().type(AgentEntity.class).id(id).now();
		
		//@Todo
		
		//agent schedule, view requests, e.t.c
		
	}
	
	@ModelMethod(functionality = AgentFunctionalities.Constants.VIEW_AGENT)
	public static List<AgentSpec> getAgents(List<Long> ids) {
		
		List<AgentSpec> result = new ArrayList<>();
		
		ofy().load().type(AgentEntity.class).ids(ids).forEach((k,v) -> {
			result.add(EntityHelper.toObjectModel(v));
		});
		
		return result;
	}

	@PlatformInternal
	public static Long getAgentOrganization(Realm realm, Long userId) {

		Long agentOrganization = null;
		
		if(realm instanceof AgentRealm) {
			agentOrganization = ofy().load().type(AgentEntity.class).id(userId).now().getAgentOrganization();
		}
		
		if(realm instanceof OrganizationAdminRealm) {
			agentOrganization = ofy().load().type(AgentOrganizationAdminEntity.class).id(userId).now()
					.getAgentOrganization();
		}

		return agentOrganization;
	}

	/**
	 * This checks if the principal is allowed to provision the specified agent
	 * profile, i.e. If he is the Admin of the Agent's Organization
	 */
	private static void validateAgentProvisioning(Long id, Long principal) {

		if(id.equals(principal)) {
			return;
		}
		
		String role = BaseUserModel.getRole(principal);
		Realm realm = RoleModel.getRealm(role);

		if (realm instanceof AdminRealm) {
			return;
		}

		AgentEntity ae = ofy().load().type(AgentEntity.class).id(id).safe();
		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(ae.getAgentOrganization())
				.safe();

		if (!aoe.getAdmin().equals(principal)) {
			throw new ResourceException(ResourceException.FAILED_VALIDATION);
		}
	}

	/**
	 * This checks if the principal is allowed to provision the specified agent
	 * organization, i.e. If he is an Agent in the Organization
	 */
	@PlatformInternal
	protected static void validateAgentOrganizationProvisioning(Long id, Long principal) {

		String role = BaseUserModel.getRole(principal);
		Realm realm = RoleModel.getRealm(role);

		if (realm instanceof AdminRealm) {
			return;
		}

		AgentOrganizationEntity aoe = ofy().load().type(AgentOrganizationEntity.class).id(id).safe();
		if (!aoe.getAgents().contains(principal)) {
			throw new ResourceException(ResourceException.FAILED_VALIDATION);
		}
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_MESSAGES)
	public static Long newAgentOrganizationMessage(AgentOrganizationMessageSpec spec) {

		AgentOrganizationMessageEntity e = EntityHelper.fromObjectModel(spec)
				.setResolution(IssueResolution.OPEN.getValue());

		Long id = ofy().save().entity(e).now().getId();

		// Add to Activity Stream

		return id;
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION_MESSAGES)
	public static void updateAgentOrganizationMessage(Long id, Long principal, IssueResolution resolution) {

		AgentOrganizationMessageEntity e = ofy().load().type(AgentOrganizationMessageEntity.class).id(id).now();

		validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		e.setResolution(resolution.getValue()).addResolutionHistory(resolution.getValue(), principal);
		ofy().save().entity(e).now();

		// Add to Activity Stream
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_MESSAGES)
	public static void deleteAgentOrganizationMessage(Long id, Long principal) {

		AgentOrganizationMessageEntity e = ofy().load().type(AgentOrganizationMessageEntity.class).id(id).now();

		validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		ofy().delete().type(AgentOrganizationMessageEntity.class).id(id).now();

		// Add to Activity Stream
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION_MESSAGE)
	public static AgentOrganizationMessageSpec getAgentOrganizationMessage(Long id, Long principal) {

		AgentOrganizationMessageEntity e = ofy().load().type(AgentOrganizationMessageEntity.class).id(id).now();

		validateAgentOrganizationProvisioning(e.getAgentOrganization(), principal);

		ofy().save().entity(e.setIsRead(true)).now();

		// Add to Activity Stream

		return EntityHelper.toObjectModel(e);
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public static Long newAgentOrganizationWhistleblowMessage(AgentOrganizationWhistleblowMessageSpec spec) {

		AgentOrganizationWhistleblowMessageEntity e = EntityHelper.fromObjectModel(spec);

		Long id = ofy().save().entity(e).now().getId();

		// Add to Activity Stream

		return id;
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public static void updateAgentOrganizationWhistleblowMessage(Long id, Long principal, IssueResolution resolution) {

		AgentOrganizationWhistleblowMessageEntity e = ofy().load().type(AgentOrganizationWhistleblowMessageEntity.class)
				.id(id).now();

		e.setResolution(resolution.getValue()).addResolutionHistory(resolution.getValue(), principal);
		ofy().save().entity(e).now();

		// Add to Activity Stream
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGES)
	public static void deleteAgentOrganizationWhistleblowMessage(Long id, Long principal) {

		ofy().delete().type(AgentOrganizationWhistleblowMessageEntity.class).id(id).now();

		// Add to Activity Stream
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.VIEW_AGENT_ORGANIZATION_WHISTLEBLOW_MESSAGE)
	public static AgentOrganizationWhistleblowMessageSpec getAgentOrganizationWhistleblowMessage(Long id,
			Long principal) {

		AgentOrganizationWhistleblowMessageEntity e = ofy().load().type(AgentOrganizationWhistleblowMessageEntity.class)
				.id(id).now();

		ofy().save().entity(e.setIsRead(true)).now();

		// Add to Activity Stream

		return EntityHelper.toObjectModel(e);
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.CREATE_AGENT_ORGANIZATION_REVIEW)
	public static Long newAgentOrganizationReview(AgentOrganizationReviewSpec spec) {

		AgentOrganizationReviewEntity e = EntityHelper.fromObjectModel(spec);

		Long id = ofy().save().entity(e).now().getId();

		// Add to Activity Stream

		return id;
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.DELETE_AGENT_ORGANIZATION_REVIEWS)
	public static void deleteAgentOrganizationReview(Long id, Long principal) {

		ofy().delete().type(AgentOrganizationReviewEntity.class).id(id);

		// Add to Activity Stream
	}

	private static String getAgentAvailabilityScheduleId(Long accountId, Integer week, Integer day) {
		return accountId + "_w_" + week + "_d_" + day;
	}

	private static String toScheduleString(Integer hh, Integer mm) {
		return hh + ":" + mm;
	}

	/**
	 * This creates a default availability schedule for the specified agent
	 * organization
	 */
	private static void createAgentOrganizationAvailabilitySchedule(Long agentOrganizationId) {

		List<AgentOrganizationAvailabilityScheduleEntity> entities = new ArrayList<AgentOrganizationAvailabilityScheduleEntity>();

		String fromTime = toScheduleString(9, 0);
		String toTime = toScheduleString(16, 0);

		for (int w = 1; w < 5; w++) {
			for (int d = 1; d < 6; d++) {

				AgentOrganizationAvailabilityScheduleEntity e = new AgentOrganizationAvailabilityScheduleEntity()
						.setId(getAgentAvailabilityScheduleId(agentOrganizationId, w, d))
						.setAgentOrganization(agentOrganizationId)
						.setBaseSchedules(FluentHashMap.forNameMap().with(fromTime, toTime))
						.setDateUpdated(Dates.now());

				entities.add(e);
			}
		}

		ofy().save().entities(entities);
	}

	/**
	 * This creates a default availability schedule for the specified agent, based
	 * on his organization's schedule
	 */
	private static void createAgentAvailabilitySchedule(Long agent) {

		Long agentOrganization = getAgentOrganization(new AgentRealm(), agent);

		List<AgentAvailabilityScheduleEntity> entities = Lists.newArrayList();

		QueryKeys<AgentOrganizationAvailabilityScheduleEntity> keys = ofy().load()
				.type(AgentOrganizationAvailabilityScheduleEntity.class)
				.filter("agentOrganization", agentOrganization.toString()).keys();

		ofy().load().type(AgentOrganizationAvailabilityScheduleEntity.class).ids(keys).forEach((k, v) -> {

			AgentAvailabilityScheduleEntity e = new AgentAvailabilityScheduleEntity()
					.setId(v.getId().replaceFirst(agentOrganization.toString(), agent.toString())).setAgent(agent)
					.setBaseSchedules(v.getBaseSchedules()).setDateUpdated(Dates.now());

			entities.add(e);
		});

		ofy().save().entities(entities);
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.UPDATE_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULE, isFeatureReady = false)
	public static void updateAgentOrganizationAvailabilitySchedule(Long principal, Long agentOrganization,
			Map<String, Map<String, String>> schedules) {

		validateAgentOrganizationProvisioning(agentOrganization, principal);
		
		List<AgentOrganizationAvailabilityScheduleEntity> entities = new ArrayList<AgentOrganizationAvailabilityScheduleEntity>();

		schedules.forEach((k, v) -> {

			AgentOrganizationAvailabilityScheduleEntity e = new AgentOrganizationAvailabilityScheduleEntity().setId(k)
					.setBaseSchedules(v).setDateUpdated(Dates.now());

			entities.add(e);
		});

		ofy().save().entities(entities);

		// add to activity stream
	}

	@ModelMethod(functionality = AgentFunctionalities.Constants.UPDATE_AGENT_AVAILABILITY_SCHEDULE, isFeatureReady = false)
	public static void updateAgentAvailabilitySchedule(Long principal, Long agent, Map<String, Map<String, String>> schedules) {

		validateAgentProvisioning(principal, agent);
		
		List<AgentAvailabilityScheduleEntity> entities = new ArrayList<AgentAvailabilityScheduleEntity>();

		schedules.forEach((k, v) -> {

			AgentAvailabilityScheduleEntity e = new AgentAvailabilityScheduleEntity().setId(k).setBaseSchedules(v)
					.setDateUpdated(Dates.now());

			entities.add(e);
		});

		ofy().save().entities(entities);

		// add to activity stream
	}

	@ModelMethod(functionality = AgentOrganizationFunctionalities.Constants.GET_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULES, isFeatureReady = false)
	public static Map<String, Map<String, String>> getAgentOrganizationAvailabilitySchedules(Long agentOrganization) {

		Map<String, Map<String, String>> result = new HashMap<>();
		EntityUtils.query(AgentOrganizationAvailabilityScheduleEntity.class, QueryFilter.get("agentOrganization", agentOrganization.toString()))
				.forEach(e -> {
					result.put(e.getId(), e.getBaseSchedules());
				});

		return result;
	}

	@ModelMethod(functionality = AgentFunctionalities.Constants.GET_AGENT_AVAILABILITY_SCHEDULES, isFeatureReady = false)
	public static Map<String, Map<String, String>> getAgentAvailabilitySchedules(Long principal, Long agent) {

		validateAgentProvisioning(agent, principal);
		
		Map<String, Map<String, String>> result = new HashMap<>();
		EntityUtils.query(AgentAvailabilityScheduleEntity.class, QueryFilter.get("agent", agent.toString()))
				.forEach(e -> {
					result.put(e.getId(), e.getBaseSchedules());
				});

		return result;
	}

	protected static Map<String, String> getAgentAvailabilitySchedule(Long agent, String scheduleId) {
		
		//AgentAvailabilityScheduleEntity e = ofy().load().type(AgentAvailabilityScheduleEntity.class).id(agent).safe();
		//return e.getBaseSchedules();
		
		String fromTime = toScheduleString(9, 0);
		String toTime = toScheduleString(16, 0);

		return FluentHashMap.forNameMap().with(fromTime, toTime);
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
