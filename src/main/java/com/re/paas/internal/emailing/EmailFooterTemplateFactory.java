package com.re.paas.internal.emailing;

import java.util.HashMap;
import java.util.Map;

import com.re.paas.api.classes.ClientResources;
import com.re.paas.api.classes.ClientResources.HtmlCharacterEntities;
import com.re.paas.api.classes.ClientResources.WebResource;
import com.re.paas.api.models.classes.UserProfileSpec;
import com.re.paas.api.templating.TemplateObjectModelFactory;
import com.re.paas.internal.entites.BaseUserEntity;

public class EmailFooterTemplateFactory extends TemplateObjectModelFactory<EmailFooterTemplate> {

	private static Map<String, WebResource> getSocialHandles(Long userId) {

		Map<String, WebResource> o = new HashMap<String, ClientResources.WebResource>();

		// BaseUserEntity e = BaseUserModel.get(userId);
		BaseUserEntity e = new BaseUserEntity().setTwitterProfile("tonyobanon").setFacebookProfile("tonyobanon")
				.setLinkedInProfile("tonyobanon").setSkypeProfile("tonyobanon");

		if (e.getFacebookProfile() != null) {
			o.put("facebook",
					WebResource.get("http://www.facebook.com/" + e.getFacebookProfile(), e.getFacebookProfile()));
		}

		if (e.getTwitterProfile() != null) {
			o.put("twitter", WebResource.get("http://www.twitter.com/" + e.getTwitterProfile(), e.getTwitterProfile()));
		}

		if (e.getLinkedInProfile() != null) {
			o.put("linkedin",
					WebResource.get("http://www.linkedin.com/" + e.getLinkedInProfile(), e.getLinkedInProfile()));
		}

		if (e.getSkypeProfile() != null) {
			o.put("skype", WebResource.get("http://www.skype.com/" + e.getSkypeProfile(), e.getSkypeProfile()));
		}

		return o;
	}

	@Override
	public EmailFooterTemplate create(EmailFooterTemplate template) {

		//if (template.getPrincipal() == null) {
		//	return null;
		//}

		//UserProfileSpec spec = BaseUserModel.getProfile(template.getPrincipal());
		UserProfileSpec spec = new UserProfileSpec()
				.setFirstName("Tony")
				.setLastName("Anyanwu")
				.setPhone(Long.parseLong("+2348165414256"))
				.setRole("Admin");

		/*
		template.setCompanyLogoURL(ConfigModel.get(ConfigKeys.ORGANIZATION_LOGO_URL))
				.setCompanyName(ConfigModel.get(ConfigKeys.ORGANIZATION_NAME))
				.setEmailConfidentialNotice(RBModel.get(template.getUserLocale(), "email_confidentiality_notice"))
				.setSenderName(spec.getFirstName() + HtmlCharacterEntities.SPACE + spec.getLastName())
				.setSenderPhone(spec.getPhone().toString()).setSenderRole(spec.getRole())
				.setSenderSocialHandles(getSocialHandles(template.getPrincipal()));
				*/
		template.setCompanyLogoURL("http://www.compute-essentials.com/favicon.ico")
		.setCompanyName("Compute Essentials")
		.setEmailConfidentialNotice("CONFIDENTIALITY NOTICE This message and the information contained in it is intended only for the personal and confidential use of the intended addressee. This message and any attachments are confidential and may be legally privileged and protected from discovery or disclosure. If you have received this message in error or if the reader of this message is not the intended recipient or an agent responsible for delivering it to an intended recipient, you are not authorized to rely on, review, disseminate, distribute or copy this message or any part of it. If you have received this message in error, please notify the sender immediately through a telephone call or e-mail, delete the message from any computer.")
		.setSenderName(spec.getFirstName() + HtmlCharacterEntities.SPACE + spec.getLastName())
		.setSenderPhone(spec.getPhone().toString()).setSenderRole(spec.getRole())
		.setSenderSocialHandles(getSocialHandles(template.getPrincipal()));

		return template;
	}

}
