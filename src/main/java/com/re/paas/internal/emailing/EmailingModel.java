package com.re.paas.internal.emailing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import com.google.common.collect.Maps;
import com.re.paas.api.classes.ClientRBRef;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.models.BaseModel;
import com.re.paas.api.models.classes.InstallOptions;
import com.re.paas.api.models.classes.MailCredentialSpec;
import com.re.paas.internal.core.keys.ConfigKeys;
import com.re.paas.internal.models.BaseUserModel;
import com.re.paas.internal.models.ConfigModel;
import com.re.paas.internal.models.LocaleModel;
import com.re.paas.internal.models.RBModel;
import com.re.paas.internal.templating.TemplatingModel;
import com.re.paas.internal.utils.JMSUtil;

public class EmailingModel implements BaseModel {

	@Override
	public String path() {
		return "core/emailing";
	}

	@Override
	public void install(InstallOptions options) {

		MailCredentialSpec spec = options.getMailCredentials();

		ConfigModel.put(ConfigKeys.MAIL_PROVIDER_URL, spec.getProviderUrl());
		ConfigModel.put(ConfigKeys.MAIL_PROVIDER_USERNAME, spec.getUsername());
		ConfigModel.put(ConfigKeys.MAIL_PROVIDER_PASSWORD, spec.getPassword());

	}

	@Override
	public void start() {

		MailCredentialSpec spec = new MailCredentialSpec().setProviderUrl(ConfigModel.get(ConfigKeys.MAIL_PROVIDER_URL))
				.setUsername(ConfigModel.get(ConfigKeys.MAIL_PROVIDER_USERNAME))
				.setPassword(ConfigModel.get(ConfigKeys.MAIL_PROVIDER_PASSWORD));

		JMSUtil.setCredentials(spec);
	}

	private static Map<String, List<String>> getLocaleGroups(List<String> recipients) {

		Map<String, List<String>> localeGroups = new HashMap<>();

		for (String r : recipients) {

			String defaultLocale = null;
			try {
				// If user is registered, use his default locale
				defaultLocale = BaseUserModel.getPreferredLocale(BaseUserModel.getUserId(r));
			} catch (NullPointerException e) {
				// else, Use platform default
				defaultLocale = LocaleModel.defaultLocale();
			}

			if (localeGroups.get(defaultLocale) == null) {
				localeGroups.put(defaultLocale, new ArrayList<>());
			}

			localeGroups.get(defaultLocale).add(r);
		}

		return localeGroups;
	}
	
	/**
	 * This groups the recipients based on their locale
	 * */
	public static Map<EmailMessageTemplate, List<String>> groupRecipients(Long principal, List<String> recipients) {

		Map<EmailMessageTemplate, List<String>> result = Maps.newHashMap();
		
		Map<String, List<String>> paramsGroups = getLocaleGroups(recipients);

		for (Entry<String, List<String>> group : paramsGroups.entrySet()) {
			result.put(new EmailMessageTemplate(principal, group.getKey()), group.getValue());
		}

		return result;
	}
	
	public static void sendMail(List<String> recipients, ClientRBRef subject,
			EmailMessageTemplate message) {

		JMSUtil util = new JMSUtil(recipients, RBModel.get(message.getLocale(), subject.toString()),
				TemplatingModel.getTemplate(message)
						.toString());

		try {
			util.send();
		} catch (MessagingException e) {
			Exceptions.throwRuntime(e);
		}

		// Add to event stream
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preInstall() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unInstall() {
		// TODO Auto-generated method stub
		
	}

}
