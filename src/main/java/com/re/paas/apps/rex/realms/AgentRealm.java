package com.re.paas.apps.rex.realms;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.annotations.ProtectionContext;
import com.re.paas.api.annotations.ProtectionContext.Factor;
import com.re.paas.api.annotations.develop.Prototype;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.apps.rex.classes.spec.AgentSpec;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.functionality.PropertyFunctionalities;
import com.re.paas.apps.rex.models.BaseAgentModel;
import com.re.paas.apps.rex.models.listables.IndexedNameTypes;
import com.re.paas.apps.rex.models.tables.AgentOrganizationTable;
import com.re.paas.apps.rex.models.tables.AgentTable;
import com.re.paas.apps.rex.sentences.ObjectTypes;
import com.re.paas.apps.shared.Functionalities;
import com.re.paas.internal.models.ApplicationModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.helpers.FormFieldRepository;
import com.re.paas.internal.models.helpers.FormFieldRepository.FormField;
import com.re.paas.internal.utils.ObjectUtils;

public class AgentRealm extends Realm {

	@Override
	public String name() {
		return "Agent";
	}

	@Override
	public Functionality[] functionalities() {

		return ObjectUtils.toArray(

				Functionalities.forUsers(), Functionalities.forPrincipal(),

				new Functionality[] {

						AgentOrganizationFunctionalities.LIST_AGENT_ORGANIZATION_MESSAGES,
						AgentOrganizationFunctionalities.UPDATE_AGENT_ORGANIZATION_MESSAGES,
						AgentOrganizationFunctionalities.DELETE_AGENT_ORGANIZATION_MESSAGES,
						AgentOrganizationFunctionalities.VIEW_AGENT_ORGANIZATION_MESSAGE,

						PropertyFunctionalities.CREATE_PROPERTY_CREATION_REQUEST,
						PropertyFunctionalities.CREATE_PROPERTY_UPDATE_REQUEST,
						PropertyFunctionalities.CREATE_PROPERTY_DELETION_REQUEST,

						PropertyFunctionalities.CREATE_PROPERTY_LISTING,
						PropertyFunctionalities.UPDATE_PROPERTY_LISTING,
						PropertyFunctionalities.DELETE_PROPERTY_LISTING,

						PropertyFunctionalities.GET_PROPERTY_LISTINGS,
						PropertyFunctionalities.UPDATE_PROPERTY_LISTING_AVAILABILITY_STATUS,

						PropertyFunctionalities.ADD_TO_USER_SAVED_LIST,
						PropertyFunctionalities.REMOVE_FROM_USER_SAVED_LIST,
						PropertyFunctionalities.GET_USER_SAVED_LIST,

						PropertyFunctionalities.CREATE_PROPERTY_PRICE_RULE,
						PropertyFunctionalities.VIEW_PROPERTY_PRICE_RULES,
						PropertyFunctionalities.UPDATE_PROPERTY_PRICE_RULE,
						PropertyFunctionalities.DELETE_PROPERTY_PRICE_RULE,

						AgentFunctionalities.UPDATE_AGENT_AVAILABILITY_SCHEDULE,
						AgentFunctionalities.GET_AGENT_AVAILABILITY_SCHEDULES });
	}

	@Override
	public Section[] onboardingForm() {
		
		return new Section[] {};
	}

	@Override
	public Integer authority() {
		return null;
	}

	@Override
	public Map<String, String> getSuggestedProfiles(Long principal, Long userId) {

		Map<String, String> keys = new HashMap<String, String>();

		AgentTable ae = ofy().load().type(AgentTable.class).id(userId).safe();
		AgentOrganizationTable aoe = ofy().load().type(AgentOrganizationTable.class).id(ae.getAgentOrganization())
				.safe();

		aoe.getAgents().forEach(e -> {
			keys.put(e.toString(), ClientRBRef.forAll("Agent", "at").toString()
					+ ClientResources.HtmlCharacterEntities.SPACE + aoe.getName());
		});
		

		return keys;
	}

	@Override
	public RealmApplicationSpec applicationSpec() {
		
		String organizationField = FormFieldRepository.getFieldId(this, FormField.ORGANIZATION_ID);
		
		return new RealmApplicationSpec()
				
				.setRequiresReview(true)
				.setReviewFunctionality(AgentFunctionalities.REVIEW_AGENT_APPLICATION)				
				.setBaseObjectType(ObjectTypes.AGENT_APPLICATION)
				.setIndexedNameType(IndexedNameTypes.AGENT_APPLICATION)
				.setListingRefField(organizationField)
				.setVariableNames(FluentArrayList.asList(organizationField))
				
				.setOnAccept((applicationId, userId) -> {

					String role = ApplicationModel.getApplicationRole(applicationId);
					Realm realm = RoleModel.getRealm(role);

					Map<FormField, String> keys = FormFieldRepository.getFieldIds(realm);
					Map<String, String> values = ApplicationModel.getFieldValues(applicationId);

					BaseAgentModel.newAgent(userId, getConsolidatedAgent(keys, values));
				});	
	}
	

	private static AgentSpec getConsolidatedAgent(Map<FormField, String> keys, Map<String, String> values) {

		Integer yearsOfExperience = Integer.parseInt(values.get(keys.get(FormField.YEARS_OF_EXPERIENCE)));

		return new AgentSpec().setYearsOfExperience(yearsOfExperience);
	}

}
