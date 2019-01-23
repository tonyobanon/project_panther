package com.re.paas.apps.rex.realms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.forms.Section;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.listable.QueryFilter;
import com.re.paas.api.realms.Realm;
import com.re.paas.api.realms.RealmApplicationSpec;
import com.re.paas.api.sentences.ObjectType;
import com.re.paas.apps.rex.classes.spec.AgentOrganizationSpec;
import com.re.paas.apps.rex.functionality.AgentFunctionalities;
import com.re.paas.apps.rex.functionality.AgentOrganizationFunctionalities;
import com.re.paas.apps.rex.models.listables.IndexedNameTypes;
import com.re.paas.apps.rex.sentences.ObjectTypes;
import com.re.paas.apps.shared.Functionalities;
import com.re.paas.internal.entites.directory.AgentOrganizationAdminEntity;
import com.re.paas.internal.entites.directory.AgentOrganizationEntity;
import com.re.paas.internal.fusion.functionalities.UserApplicationFunctionalities;
import com.re.paas.internal.models.ApplicationModel;
import com.re.paas.internal.models.BaseAgentModel;
import com.re.paas.internal.models.RoleModel;
import com.re.paas.internal.models.helpers.EntityUtils;
import com.re.paas.internal.models.helpers.FormFieldRepository;
import com.re.paas.internal.models.helpers.FormFieldRepository.FormField;
import com.re.paas.internal.realms.AdminRealm;
import com.re.paas.internal.utils.ObjectUtils;

public class OrganizationAdminRealm implements Realm {

	@Override
	public String name() {
		return "Organization Admin";
	}

	@Override
	public Functionality[] functionalities() {

		return ObjectUtils.toArray(

				Functionalities.forPrincipal(), new Functionality[] {

						AgentFunctionalities.VIEW_AGENT_APPLICATIONS, AgentFunctionalities.REVIEW_AGENT_APPLICATION,

						AgentOrganizationFunctionalities.UPDATE_AGENT_ORGANIZATION,

						AgentFunctionalities.DELETE_AGENT,

						AgentOrganizationFunctionalities.UPDATE_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULE,
						AgentOrganizationFunctionalities.GET_AGENT_ORGANIZATION_AVAILABILITY_SCHEDULES,

				});
	}

	@Override
	public Section[] onboardingForm() {
		return new Section[] {};
	}

	@Override
	public Integer authority() {
		return 15;
	}

	@Override
	public Map<String, String> getSuggestedProfiles(Long principal, Long userId) {

		Map<String, String> keys = new HashMap<String, String>();

		AgentOrganizationAdminEntity oae = ofy().load().type(AgentOrganizationAdminEntity.class).id(userId).safe();
		AgentOrganizationEntity oe = ofy().load().type(AgentOrganizationEntity.class).id(oae.getAgentOrganization())
				.safe();

		EntityUtils.query(AgentOrganizationEntity.class, QueryFilter.get("city", oe.getCity())).forEach(e -> {
			keys.put(e.getAdmin().toString(), ClientRBRef.forAll("Admin_agent", "at").toString()
					+ ClientResources.HtmlCharacterEntities.SPACE + oe.getName());
		});

		return keys;
	}

	@Override
	public RealmApplicationSpec applicationSpec() {

		String organizationField = FormFieldRepository.getFieldId(this, FormField.ORGANIZATION_ID);

		return new RealmApplicationSpec()

				.setRequiresReview(true)
				.setReviewFunctionality(AgentOrganizationFunctionalities.REVIEW_ORGANIZATION_ADMIN_APPLICATION)
				.setBaseObjectType(ObjectTypes.ORGANIZATION_ADMIN_APPLICATION)
				.setIndexedNameType(IndexedNameTypes.ORGANIZATION_ADMIN_APPLICATION)
				.setVariableNames(FluentArrayList.asList(organizationField))
				
				.setOnAccept((applicationId, userId) -> {

					String role = ApplicationModel.getApplicationRole(applicationId);
					Realm realm = RoleModel.getRealm(role);

					Map<FormField, String> keys = FormFieldRepository.getFieldIds(realm);
					Map<String, String> values = ApplicationModel.getFieldValues(applicationId);

					Long agentOrganization = BaseAgentModel
							.newAgentOrganization(getConsolidatedAgentOrganization(keys, values).setAdmin(userId));

					BaseAgentModel.newAgentOrganizationAdmin(userId, agentOrganization);
					
					// Update the application to include the ORGANIZATION_ID
					values.put(organizationField, agentOrganization.toString());
					ApplicationModel.updateApplication(applicationId, values);
				});
	}

	private static AgentOrganizationSpec getConsolidatedAgentOrganization(Map<FormField, String> keys,
			Map<String, String> values) {

		return new AgentOrganizationSpec()
				.setName(values.get(keys.get(FormField.ORGANIZATION_NAME)))
				.setEmail(values.get(keys.get(FormField.ORGANIZATION_EMAIL)))
				.setPhone(Long.parseLong(values.get(keys.get(FormField.ORGANIZATION_PHONE))))
				.setLogo(values.get(keys.get(FormField.ORGANIZATION_LOGO)))
				.setAddress(values.get(keys.get(FormField.ORGANIZATION_ADDRESS)))
				.setPostalCode(Integer.parseInt(values.get(keys.get(FormField.ORGANIZATION_POSTAL_CODE))))
				.setCity(Integer.parseInt(values.get(keys.get(FormField.ORGANIZATION_CITY))))
				.setTerritory(values.get(keys.get(FormField.ORGANIZATION_STATE)))
				.setCountry(values.get(keys.get(FormField.ORGANIZATION_COUNTRY)));
	}

}
