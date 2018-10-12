package com.re.paas.internal.emailing;

import java.util.List;

import com.google.common.base.Joiner;
import com.re.paas.api.classes.ClientResources.WebResource;
import com.re.paas.api.templating.Exclude;
import com.re.paas.api.templating.TemplateObjectModel;
import com.re.paas.api.utils.Utils;


public class EmailMessageTemplate extends TemplateObjectModel {

	@Exclude
	private static Object template;
	
	private String pageTitle;
	private String bodyPreview;
	private List<String> preActionText;
	private List<String> postActionText;
	private WebResource action;
	private String companyName;
	private String companyAddress;
	private boolean isMarketing;
	private String marketingOptOutUrl;
	private String softwareVendorName;
	private String softwareVendorUrl;

	protected EmailFooterTemplate footer;

	private Long principal;
	private String locale;
	
	@SuppressWarnings("unused")
	private EmailMessageTemplate() {
	}
	
	public EmailMessageTemplate(Long principal, String locale) {
		
		this.principal = principal;
		this.locale = locale;
		
		this.footer = new EmailFooterTemplate(principal, locale);
	}
	
	@Override
	public String templateName() {
		return "templates/email-message.tpl";
	}
	
	protected Long getPrincipal() {
		return principal;
	}

	protected String getLocale() {
		return locale;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public EmailMessageTemplate setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
		return this;
	}

	public String getBodyPreview() {
		return bodyPreview;
	}

	public EmailMessageTemplate setBodyPreview(String bodyPreview) {
		this.bodyPreview = bodyPreview;
		return this;
	}

	public List<String> getPreActionText() {
		return preActionText;
	}

	public EmailMessageTemplate setPreActionText(List<String> preActionText) {
		this.preActionText = preActionText;
		this.bodyPreview = Utils.truncate(Joiner.on(" ").join(preActionText), 10);
		return this;
	}

	public List<String> getPostActionText() {
		return postActionText;
	}

	public EmailMessageTemplate setPostActionText(List<String> postActionText) {
		this.postActionText = postActionText;
		return this;
	}

	public WebResource getAction() {
		return action;
	}

	public EmailMessageTemplate setAction(WebResource action) {
		this.action = action;
		return this;
	}

	public String getCompanyName() {
		return companyName;
	}

	public EmailMessageTemplate setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public EmailMessageTemplate setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
		return this;
	}

	public boolean getIsMarketing() {
		return isMarketing;
	}

	public EmailMessageTemplate setIsMarketing(boolean isMarketing) {
		this.isMarketing = isMarketing;
		return this;
	}

	public String getMarketingOptOutUrl() {
		return marketingOptOutUrl;
	}

	public EmailMessageTemplate setMarketingOptOutUrl(String marketingOptOutUrl) {
		this.marketingOptOutUrl = marketingOptOutUrl;
		return this;
	}

	public String getSoftwareVendorName() {
		return softwareVendorName;
	}

	public EmailMessageTemplate setSoftwareVendorName(String softwareVendorName) {
		this.softwareVendorName = softwareVendorName;
		return this;
	}

	public String getSoftwareVendorUrl() {
		return softwareVendorUrl;
	}

	public EmailMessageTemplate setSoftwareVendorUrl(String softwareVendorUrl) {
		this.softwareVendorUrl = softwareVendorUrl;
		return this;
	}

	@Override
	protected Object getTemplate() {
		return template;
	}

	@Override
	public void setTemplate(Object template) {
		EmailMessageTemplate.template = template;
	}
}
