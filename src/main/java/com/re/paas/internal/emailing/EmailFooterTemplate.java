package com.re.paas.internal.emailing;

import java.util.Map;

import com.re.paas.api.classes.ClientResources.WebResource;
import com.re.paas.api.templating.Exclude;
import com.re.paas.api.templating.TemplateObjectModel;

public class EmailFooterTemplate extends TemplateObjectModel {

	@Exclude
	private static Object template;

	private Long principal;
	private String userLocale;

	private String senderName;
	private String senderRole;
	private String companyName;
	private String senderPhone;
	private Map<String, WebResource> senderSocialHandles;
	private String companyLogoURL;
	private String emailConfidentialNotice;

	@SuppressWarnings("unused")
	private EmailFooterTemplate() {
	}

	public EmailFooterTemplate(Long principal, String userLocale) {
		this.principal = principal;
		this.userLocale = userLocale;
	}

	public Long getPrincipal() {
		return principal;
	}

	public String getUserLocale() {
		return userLocale;
	}

	@Override
	public String templateName() {
		return "templates/email-footer.tpl";
	}

	public String getSenderName() {
		return senderName;
	}

	public EmailFooterTemplate setSenderName(String senderName) {
		this.senderName = senderName;
		return this;
	}

	public String getSenderRole() {
		return senderRole;
	}

	public EmailFooterTemplate setSenderRole(String senderRole) {
		this.senderRole = senderRole;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public EmailFooterTemplate setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public EmailFooterTemplate setSenderPhone(String senderPhone) {
		this.senderPhone = senderPhone;
		return this;
	}

	public Map<String, WebResource> getSenderSocialHandles() {
		return senderSocialHandles;
	}

	public EmailFooterTemplate setSenderSocialHandles(Map<String, WebResource> senderSocialHandles) {
		this.senderSocialHandles = senderSocialHandles;
		return this;
	}

	public String getCompanyLogoURL() {
		return companyLogoURL;
	}

	public EmailFooterTemplate setCompanyLogoURL(String companyLogoURL) {
		this.companyLogoURL = companyLogoURL;
		return this;
	}

	public String getEmailConfidentialNotice() {
		return emailConfidentialNotice;
	}

	public EmailFooterTemplate setEmailConfidentialNotice(String emailConfidentialNotice) {
		this.emailConfidentialNotice = emailConfidentialNotice;
		return this;
	}

	@Override
	protected Object getTemplate() {
		return template;
	}

	@Override
	public void setTemplate(Object template) {
		EmailFooterTemplate.template = template;
	}
}
