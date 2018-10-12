package com.re.paas.internal.emailing;

import com.re.paas.api.templating.TemplateObjectModelFactory;
import com.re.paas.internal.Application;

public class EmailMessageTemplateFactory extends TemplateObjectModelFactory<EmailMessageTemplate> {

	@Override
	public EmailMessageTemplate create(EmailMessageTemplate template) {

		/*
		template
		.setCompanyName(ConfigModel.get(ConfigKeys.ORGANIZATION_NAME))
		.setSoftwareVendorName(AppDelegate.SOFTRWARE_VENDER_EMAIL)
		.setSoftwareVendorUrl(AppDelegate.SOFTRWARE_VENDER_NAME);
		*/
		
		template
		.setCompanyName("Compute Essentials Technologies")
		.setCompanyAddress("34, Victoria Street, Ojota, Lagos")
		.setSoftwareVendorName(Application.SOFTWARE_VENDOR_EMAIL)
		.setSoftwareVendorUrl(Application.SOFTWARE_VENDOR_NAME);
		
		return template;
	}

}
